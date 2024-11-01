/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.scheduler.SchedulerTaskRequest;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskVO;

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
