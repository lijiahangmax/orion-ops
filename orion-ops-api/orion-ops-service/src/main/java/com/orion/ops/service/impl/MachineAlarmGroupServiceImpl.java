package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.MachineAlarmGroupDAO;
import com.orion.ops.entity.domain.MachineAlarmGroupDO;
import com.orion.ops.service.api.MachineAlarmGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
@Service("machineAlarmGroupService")
public class MachineAlarmGroupServiceImpl implements MachineAlarmGroupService {

    @Resource
    private MachineAlarmGroupDAO machineAlarmGroupDAO;

    @Override
    public List<MachineAlarmGroupDO> selectByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getMachineId, machineId);
        return machineAlarmGroupDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getMachineId, machineId);
        return machineAlarmGroupDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .in(MachineAlarmGroupDO::getMachineId, machineIdList);
        return machineAlarmGroupDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByGroupId(Long groupId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getGroupId, groupId);
        return machineAlarmGroupDAO.delete(wrapper);
    }

}
