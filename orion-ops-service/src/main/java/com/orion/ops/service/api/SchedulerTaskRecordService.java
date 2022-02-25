package com.orion.ops.service.api;

/**
 * <p>
 * 调度任务执行日志 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskRecordService {

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 创建调度明细
     *
     * @param taskId taskId
     * @return recordId
     */
    Long createTaskRecord(Long taskId);

}
