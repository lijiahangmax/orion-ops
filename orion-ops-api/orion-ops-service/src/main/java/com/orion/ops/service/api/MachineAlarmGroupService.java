package com.orion.ops.service.api;

import com.orion.ops.entity.domain.MachineAlarmGroupDO;

import java.util.List;

/**
 * 机器报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
public interface MachineAlarmGroupService {

    /**
     * 通过机器id查询
     *
     * @param machineId machineId
     * @return rows
     */
    List<MachineAlarmGroupDO> selectByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineId machineId
     * @return effect
     */
    Integer deleteByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * 通过报警组id删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

}
