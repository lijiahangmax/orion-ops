package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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

    @Override
    public Long addUpdateEnv(MachineEnvRequest request) {
        Long id = request.getId();
        MachineEnvDO entity = new MachineEnvDO();
        entity.setId(request.getId());
        entity.setMachineId(request.getId());
        entity.setAttrKey(request.getKey());
        entity.setAttrValue(request.getValue());
        entity.setDescription(request.getDescription());
        if (id == null) {
            entity.setForbidDelete(Const.FORBID_DELETE_CAN);
            machineEnvDAO.insert(entity);
            return entity.getId();
        } else {
            return (long) machineEnvDAO.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteEnv(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            MachineEnvDO env = machineEnvDAO.selectById(id);
            Valid.eq(Const.FORBID_DELETE_CAN, env.getForbidDelete(), "{} 禁止删除", env.getAttrKey());
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
                MachineEnvDO targetEnv = targetOption.get();
                targetEnv.setAttrValue(sourceEnv.getAttrValue());
                targetEnv.setDescription(sourceEnv.getDescription());
                effect += machineEnvDAO.updateById(targetEnv);
            } else {
                // 插入
                MachineEnvDO insertEnv = new MachineEnvDO();
                insertEnv.setMachineId(targetMachineId);
                insertEnv.setAttrKey(sourceEnv.getAttrKey());
                insertEnv.setAttrValue(sourceEnv.getAttrValue());
                insertEnv.setDescription(sourceEnv.getDescription());
                insertEnv.setForbidDelete(Const.FORBID_DELETE_CAN);
                effect += machineEnvDAO.insert(insertEnv);
            }
        }
        return effect;
    }

    @Override
    public DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request) {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .like(Objects.nonNull(request.getKey()), MachineEnvDO::getAttrKey, request.getKey())
                .like(Objects.nonNull(request.getValue()), MachineEnvDO::getAttrValue, request.getValue())
                .like(Objects.nonNull(request.getDescription()), MachineEnvDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getMachineId()), MachineEnvDO::getMachineId, request.getMachineId())
                .orderByAsc(MachineEnvDO::getId);
        return DataQuery.of(machineEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineEnvVO.class);
    }

    @Override
    public MutableLinkedHashMap<String, String> getMachineEnv(Long machineId) {
        MutableLinkedHashMap<String, String> env = new MutableLinkedHashMap<>();
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, machineId)
                .orderByAsc(MachineEnvDO::getId);
        machineEnvDAO.selectList(wrapper).forEach(e -> {
            env.put(e.getAttrKey(), e.getAttrValue());
        });
        return env;
    }

    @Override
    public void initEnv(Long machineId) {
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        List<String> keys = MachineEnvAttr.getTargetKeys();
        String home = this.getHomePath(machine.getUsername());
        for (String key : keys) {
            MachineEnvDO env = new MachineEnvDO();
            MachineEnvAttr attr = MachineEnvAttr.of(key);
            env.setMachineId(machineId);
            env.setDescription(attr.getDescription());
            env.setAttrKey(attr.name());
            env.setForbidDelete(Const.FORBID_DELETE_NOT);
            switch (attr) {
                case LOG_PATH:
                    env.setAttrValue(home + "logs/");
                    break;
                case DIST_PATH:
                    env.setAttrValue(home + "dist/");
                    break;
                case TEMP_PATH:
                    env.setAttrValue(home + "temp/");
                    break;
                default:
                    break;
            }
            machineEnvDAO.insert(env);
        }
    }

    /**
     * 获取环境根目录
     *
     * @param username 用户名
     * @return 目录
     */
    private String getHomePath(String username) {
        if (Const.ROOT.equals(username)) {
            return "/" + Const.ROOT + "/" + Const.ORION_OPS + "/";
        } else {
            return "/home/" + username + "/" + Const.ORION_OPS + "/";
        }
    }

}
