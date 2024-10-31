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
package cn.orionsec.ops.task.impl;

import cn.orionsec.kit.lang.utils.Valid;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.common.SerialType;
import cn.orionsec.ops.dao.SchedulerTaskDAO;
import cn.orionsec.ops.entity.domain.SchedulerTaskDO;
import cn.orionsec.ops.handler.scheduler.ITaskProcessor;
import cn.orionsec.ops.service.api.SchedulerTaskRecordService;
import lombok.extern.slf4j.Slf4j;

/**
 * 调度任务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 17:32
 */
@Slf4j
public class SchedulerTaskImpl implements Runnable {

    private static final SchedulerTaskDAO schedulerTaskDAO = SpringHolder.getBean(SchedulerTaskDAO.class);

    private static final SchedulerTaskRecordService schedulerTaskRecordService = SpringHolder.getBean(SchedulerTaskRecordService.class);

    private final Long id;

    public SchedulerTaskImpl(Long id) {
        this.id = id;
    }

    @Override
    public void run() {
        log.info("定制调度任务-触发 taskId: {}, time: {}", id, Dates.current());
        // 查询任务
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task);
        // 创建任务
        Long recordId = schedulerTaskRecordService.createTaskRecord(id);
        // 执行任务
        ITaskProcessor processor = ITaskProcessor.with(recordId, SerialType.of(task.getSerializeType()));
        SchedulerPools.SCHEDULER_TASK_MAIN_SCHEDULER.execute(processor);
    }

}
