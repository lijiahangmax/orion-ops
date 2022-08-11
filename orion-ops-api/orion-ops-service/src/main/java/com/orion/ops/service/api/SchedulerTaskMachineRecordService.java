package com.orion.ops.service.api;

import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;

import java.util.List;

/**
 * <p>
 * 调度任务执行明细机器详情 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskMachineRecordService {

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 recordId 查询机器明细
     *
     * @param recordId recordId
     * @return rows
     */
    List<SchedulerTaskMachineRecordDO> selectByRecordId(Long recordId);

    /**
     * 获取任务机器执行日志路径
     *
     * @param machineRecordId machineRecordId
     * @return path
     */
    String getTaskMachineLogPath(Long machineRecordId);

}
