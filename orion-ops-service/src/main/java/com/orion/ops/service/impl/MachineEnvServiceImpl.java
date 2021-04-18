package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Pager;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnvAttr;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        entity.setAttrValue(request.getDescription());
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
    public Integer deleteById(MachineEnvRequest request) {
        Long id = request.getId();
        MachineEnvDO env = machineEnvDAO.selectById(id);
        Valid.eq(Const.FORBID_DELETE_CAN, env.getForbidDelete(), "禁止删除");
        return machineEnvDAO.deleteById(id);
    }

    @Override
    public DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request) {
        Pager<MachineEnvVO> pager = Pager.of(request);
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .like(Objects.nonNull(request.getKey()), MachineEnvDO::getAttrKey, request.getKey())
                .like(Objects.nonNull(request.getValue()), MachineEnvDO::getAttrValue, request.getValue())
                .like(Objects.nonNull(request.getDescription()), MachineEnvDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getMachineId()), MachineEnvDO::getMachineId, request.getMachineId())
                .orderByAsc(MachineEnvDO::getId);
        Integer count = machineEnvDAO.selectCount(wrapper);
        pager.setTotal(count);
        boolean next = pager.hasMoreData();
        if (next) {
            wrapper.last(pager.getSql());
            List<MachineEnvVO> rows = machineEnvDAO.selectList(wrapper).stream()
                    .map(p -> {
                        MachineEnvVO vo = new MachineEnvVO();
                        vo.setId(p.getId());
                        vo.setMachineId(p.getMachineId());
                        vo.setKey(p.getAttrKey());
                        vo.setValue(p.getAttrValue());
                        vo.setDescription(p.getDescription());
                        vo.setCreateTime(p.getCreateTime());
                        vo.setUpdateTime(p.getUpdateTime());
                        return vo;
                    }).collect(Collectors.toList());
            pager.setRows(rows);
        }
        return DataGrid.of(pager);
    }

    @Override
    public void initEnv(Long machineId) {
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        List<String> keys = EnvAttr.getTargetKeys();
        String home;
        String username = machine.getUsername();
        if (Const.ROOT.equals(username)) {
            home = "/" + Const.ROOT + "/ops/";
        } else {
            home = "/home/" + username + "/ops/";
        }
        for (String key : keys) {
            MachineEnvDO env = new MachineEnvDO();
            EnvAttr attr = EnvAttr.of(key);
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
                default:
                    break;
            }
            machineEnvDAO.insert(env);
        }
    }

}
