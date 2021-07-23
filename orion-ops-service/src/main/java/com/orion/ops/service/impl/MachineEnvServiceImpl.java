package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Charsets;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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

    @Override
    public Long addEnv(MachineEnvRequest request) {
        // 查询
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, request.getMachineId())
                .eq(MachineEnvDO::getAttrKey, request.getKey())
                .last(Const.LIMIT_1);
        MachineEnvDO env = machineEnvDAO.selectOne(wrapper);
        if (env != null) {
            // 修改
            Long id = env.getId();
            request.setId(id);
            this.updateEnv(request);
            return id;
        }
        // 新增
        MachineEnvDO insert = new MachineEnvDO();
        insert.setMachineId(request.getMachineId());
        insert.setAttrKey(request.getKey().trim());
        insert.setAttrValue(request.getValue());
        insert.setDescription(request.getDescription());
        machineEnvDAO.insert(insert);
        // 插入历史值
        Long id = insert.getId();
        historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, Const.ADD, null, request.getValue());
        return id;
    }

    @Override
    public Integer updateEnv(MachineEnvRequest request) {
        // 查询
        Long id = request.getId();
        MachineEnvDO before = machineEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        // 检查是否修改了值
        String beforeValue = before.getAttrValue();
        String afterValue = request.getValue();
        if (afterValue != null && !afterValue.equals(beforeValue)) {
            historyValueService.addHistory(id, HistoryValueType.MACHINE_ENV, Const.UPDATE, beforeValue, afterValue);
        }
        // 判断是否是宿主机系统环境变量
        if (Const.HOST_MACHINE_ID.equals(before.getMachineId())) {
            MachineEnvAttr env = MachineEnvAttr.of(before.getAttrKey());
            if (env != null) {
                env.setValue(afterValue);
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
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.ENV_ABSENT);
            String key = env.getAttrKey();
            Valid.isTrue(MachineEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            effect += machineEnvDAO.deleteById(id);
        }
        return effect;
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
                .eq(MachineEnvDO::getMachineId, request.getMachineId())
                .orderByAsc(MachineEnvDO::getId);
        return DataQuery.of(machineEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineEnvVO.class);
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
        MutableLinkedHashMap<String, String> env = Maps.newLinkedMutableMap();
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public void initEnv(Long machineId) {
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        List<String> keys = MachineEnvAttr.getTargetKeys();
        String home = this.getEnvPath(machine.getUsername());
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

    /**
     * 获取环境根目录
     *
     * @param username 用户名
     * @return 目录
     */
    private String getEnvPath(String username) {
        if (Const.ROOT.equals(username)) {
            return "/" + Const.ROOT + "/" + Const.ORION_OPS + "/";
        } else {
            return "/home/" + username + "/" + Const.ORION_OPS + "/";
        }
    }

}
