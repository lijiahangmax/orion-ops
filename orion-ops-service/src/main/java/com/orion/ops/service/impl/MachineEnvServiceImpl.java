package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.*;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.history.HistoryOperator;
import com.orion.ops.consts.history.HistoryValueType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.machine.MachineTargetEnvAttr;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.ApplicationVcsService;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.Charsets;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:44
 */
@Service("machineEnvService")
public class MachineEnvServiceImpl implements MachineEnvService {

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private HistoryValueService historyValueService;

    @Resource
    private ApplicationVcsService applicationVcsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEnv(MachineEnvRequest request) {
        // 查询
        Long machineId = request.getMachineId();
        String key = request.getKey();
        // 重复检查
        MachineEnvDO env = machineEnvDAO.selectOneRel(machineId, key);
        // 修改
        if (env != null) {
            SpringHolder.getBean(MachineEnvService.class).updateEnv(env, request);
            return env.getId();
        }
        // 新增
        MachineEnvDO insert = new MachineEnvDO();
        insert.setMachineId(machineId);
        insert.setAttrKey(key);
        insert.setAttrValue(request.getValue());
        insert.setDescription(request.getDescription());
        machineEnvDAO.insert(insert);
        // 插入历史值
        Long id = insert.getId();
        historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.ADD, null, request.getValue());
        return id;
    }

    @Override
    public Integer updateEnv(MachineEnvRequest request) {
        // 查询
        Long id = request.getId();
        MachineEnvDO before = machineEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        return SpringHolder.getBean(MachineEnvService.class).updateEnv(before, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateEnv(MachineEnvDO before, MachineEnvRequest request) {
        // 检查是否修改了值
        Long id = before.getId();
        String beforeValue = before.getAttrValue();
        String afterValue = request.getValue();
        if (Const.IS_DELETED.equals(before.getDeleted())) {
            // 设置新增历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.ADD, null, afterValue);
            // 恢复
            machineEnvDAO.setDeleted(id, Const.NOT_DELETED);
        } else if (afterValue != null && !afterValue.equals(beforeValue)) {
            // 检查是否修改了值 增加历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.UPDATE, beforeValue, afterValue);
        }
        // 判断是否是宿主机系统环境变量 同步更新对象
        if (Const.HOST_MACHINE_ID.equals(before.getMachineId())) {
            MachineEnvAttr env = MachineEnvAttr.of(before.getAttrKey());
            if (env != null) {
                env.setValue(afterValue);
                if (afterValue != null && !afterValue.equals(beforeValue)) {
                    if (MachineEnvAttr.VCS_PATH.equals(env)) {
                        // 如果修改的是 vcs 则修改 vcs 状态
                        applicationVcsService.syncVcsStatus();
                    }
                }
            }
        }
        // 修改
        MachineEnvDO update = new MachineEnvDO();
        update.setId(id);
        update.setAttrValue(afterValue);
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return machineEnvDAO.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteEnv(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            // 获取元数据
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.ENV_ABSENT);
            String key = env.getAttrKey();
            if (Const.HOST_MACHINE_ID.equals(env.getMachineId())) {
                Valid.isTrue(MachineEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            } else {
                Valid.isTrue(MachineTargetEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            }
            // 删除
            effect += machineEnvDAO.deleteById(id);
            // 插入历史值
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, HistoryOperator.DELETE, env.getAttrValue(), null);
        }
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnv(Long machineId, Map<String, String> env) {
        MachineEnvService self = SpringHolder.getBean(MachineEnvService.class);
        // 倒排
        List<Map.Entry<String, String>> entries = Lists.newList(env.entrySet());
        for (int i = entries.size() - 1; i >= 0; i--) {
            // 更新
            Map.Entry<String, String> entry = entries.get(i);
            MachineEnvRequest request = new MachineEnvRequest();
            request.setMachineId(machineId);
            request.setKey(entry.getKey());
            request.setValue(entry.getValue());
            self.addEnv(request);
        }
    }

    @Override
    public Integer mergeEnv(Long sourceMachineId, Long targetMachineId) {
        LambdaQueryWrapper<MachineEnvDO> sourceWrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, sourceMachineId);
        LambdaQueryWrapper<MachineEnvDO> targetWrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, targetMachineId);
        List<MachineEnvDO> sourceEnvList = machineEnvDAO.selectList(sourceWrapper);
        List<MachineEnvDO> targetEnvList = machineEnvDAO.selectList(targetWrapper);
        Valid.notEmpty(sourceEnvList);
        Valid.notEmpty(targetEnvList);
        int effect = 0;
        for (MachineEnvDO sourceEnv : sourceEnvList) {
            Optional<MachineEnvDO> targetOption = targetEnvList.stream()
                    .filter(t -> t.getAttrKey().equals(sourceEnv.getAttrKey()))
                    .findFirst();
            if (targetOption.isPresent()) {
                // 更新
                MachineEnvRequest update = new MachineEnvRequest();
                update.setId(targetOption.get().getId());
                update.setValue(sourceEnv.getAttrValue());
                update.setDescription(sourceEnv.getDescription());
                effect += this.updateEnv(update);
            } else {
                // 插入
                MachineEnvDO insertEnv = new MachineEnvDO();
                insertEnv.setMachineId(targetMachineId);
                insertEnv.setAttrKey(sourceEnv.getAttrKey());
                insertEnv.setAttrValue(sourceEnv.getAttrValue());
                insertEnv.setDescription(sourceEnv.getDescription());
                effect += machineEnvDAO.insert(insertEnv);
            }
        }
        return effect;
    }

    @Override
    public DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .like(Strings.isNotBlank(request.getKey()), MachineEnvDO::getAttrKey, request.getKey())
                .like(Strings.isNotBlank(request.getValue()), MachineEnvDO::getAttrValue, request.getValue())
                .like(Strings.isNotBlank(request.getDescription()), MachineEnvDO::getDescription, request.getDescription())
                .eq(MachineEnvDO::getMachineId, request.getMachineId())
                .orderByDesc(MachineEnvDO::getUpdateTime);
        return DataQuery.of(machineEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineEnvVO.class);
    }

    @Override
    public MachineEnvVO getEnvDetail(Long id) {
        MachineEnvDO env = machineEnvDAO.selectById(id);
        Valid.notNull(env, MessageConst.UNKNOWN_DATA);
        return Converts.to(env, MachineEnvVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncMachineEnv(Long id, Long machineId, List<Long> targetMachineIdList) {
        MachineEnvService self = SpringHolder.getBean(MachineEnvService.class);
        List<MachineEnvDO> envList;
        if (Const.N_N_L_1.equals(id)) {
            // 全量同步
            LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                    .eq(MachineEnvDO::getMachineId, machineId)
                    .orderByAsc(MachineEnvDO::getUpdateTime);
            envList = machineEnvDAO.selectList(wrapper);
        } else {
            // 查询数据
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.UNKNOWN_DATA);
            envList = Lists.singleton(env);
        }
        // 同步
        for (Long targetMachineId : targetMachineIdList) {
            for (MachineEnvDO syncEnv : envList) {
                MachineEnvRequest request = new MachineEnvRequest();
                request.setMachineId(targetMachineId);
                request.setKey(syncEnv.getAttrKey());
                request.setValue(syncEnv.getAttrValue());
                request.setDescription(syncEnv.getDescription());
                self.addEnv(request);
            }
        }
    }

    @Override
    public String getMachineEnv(Long machineId, String env) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .eq(MachineEnvDO::getAttrKey, env)
                .last(Const.LIMIT_1);
        return Optional.ofNullable(machineEnvDAO.selectOne(wrapper))
                .map(MachineEnvDO::getAttrValue)
                .orElse(null);
    }

    @Override
    public MutableLinkedHashMap<String, String> getMachineEnv(Long machineId) {
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public MutableLinkedHashMap<String, String> getFullMachineEnv(Long machineId) {
        // 查询机器
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.MACHINE_NAME, machine.getMachineName());
        env.put(EnvConst.MACHINE_TAG, machine.getMachineTag());
        env.put(EnvConst.MACHINE_HOST, machine.getMachineHost());
        env.put(EnvConst.MACHINE_PORT, machine.getSshPort() + Strings.EMPTY);
        env.put(EnvConst.MACHINE_USERNAME, machine.getUsername());
        // 查询环境变量
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> env.put(EnvConst.MACHINE_PREFIX + e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public void initEnv(Long machineId) {
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        List<String> keys = MachineTargetEnvAttr.getKeys();
        String home = PathBuilders.getEnvPath(machine.getUsername());
        for (String key : keys) {
            MachineEnvDO env = new MachineEnvDO();
            MachineEnvAttr attr = MachineEnvAttr.of(key);
            env.setMachineId(machineId);
            env.setDescription(attr.getDescription());
            env.setAttrKey(attr.getKey());
            switch (attr) {
                case LOG_PATH:
                    env.setAttrValue(home + Const.LOG_PATH);
                    break;
                case DIST_PATH:
                    env.setAttrValue(home + Const.DIST_PATH);
                    break;
                case TEMP_PATH:
                    env.setAttrValue(home + Const.TEMP_PATH);
                    break;
                case TAIL_OFFSET:
                    env.setAttrValue(Const.TAIL_OFFSET_LINE + Strings.EMPTY);
                    break;
                case TAIL_CHARSET:
                case SFTP_CHARSET:
                    env.setAttrValue(Const.UTF_8);
                    break;
                default:
                    break;
            }
            machineEnvDAO.insert(env);
        }
    }

    @Override
    public Integer deleteEnvByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId);
        return machineEnvDAO.delete(wrapper);
    }

    @Override
    public String getSftpCharset(Long machineId) {
        // 查询环境
        String charset = this.getMachineEnv(machineId, MachineEnvAttr.SFTP_CHARSET.getKey());
        if (!Charsets.isSupported(charset)) {
            charset = Const.UTF_8;
        }
        return charset;
    }

    @Override
    public String getMachineTailMode(Long machineId) {
        if (Const.HOST_MACHINE_ID.equals(machineId)) {
            String mode = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_MODE.getKey());
            return FileTailMode.of(mode, true).getMode();
        } else {
            return FileTailMode.TAIL.getMode();
        }
    }

    @Override
    public Integer getTailOffset(Long machineId) {
        String offset = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_OFFSET.getKey());
        if (Strings.isInteger(offset)) {
            return Integer.valueOf(offset);
        } else {
            return Const.TAIL_OFFSET_LINE;
        }
    }

    @Override
    public String getTailCharset(Long machineId) {
        String charset = this.getMachineEnv(machineId, MachineEnvAttr.TAIL_CHARSET.getKey());
        if (Charsets.isSupported(charset)) {
            return charset;
        } else {
            return Const.UTF_8;
        }
    }

}
