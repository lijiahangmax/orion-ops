package com.orion.ops.service.api;

import com.orion.ops.entity.domain.SchedulerTaskMachineDO;

import java.util.List;

/**
 * <p>
 * 调度任务机器 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskMachineService {

    /**
     * 通过 taskId 查询
     *
     * @param taskId taskId
     * @return rows
     */
    List<SchedulerTaskMachineDO> selectByTaskId(Long taskId);

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 机器id 删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

}
