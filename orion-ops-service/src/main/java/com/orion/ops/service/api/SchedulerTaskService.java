package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.SchedulerTaskRequest;
import com.orion.ops.entity.vo.SchedulerTaskVO;

/**
 * <p>
 * 调度任务 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskService {

    /**
     * 添加任务
     *
     * @param request request
     * @return id
     */
    Long addTask(SchedulerTaskRequest request);

    /**
     * 修改任务
     *
     * @param request request
     * @return effect
     */
    Integer updateTask(SchedulerTaskRequest request);

    /**
     * 任务详情
     *
     * @param id id
     * @return row
     */
    SchedulerTaskVO getTaskDetail(Long id);

    /**
     * 任务列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<SchedulerTaskVO> getTaskList(SchedulerTaskRequest request);

    /**
     * 更新状态
     *
     * @param id     id
     * @param status status
     * @return effect
     */
    Integer updateTaskStatus(Long id, Integer status);

    /**
     * 删除任务
     *
     * @param id id
     * @return effect
     */
    Integer deleteTask(Long id);

    /**
     * 手动触发任务
     *
     * @param id id
     */
    void manualTriggerTask(Long id);

}
