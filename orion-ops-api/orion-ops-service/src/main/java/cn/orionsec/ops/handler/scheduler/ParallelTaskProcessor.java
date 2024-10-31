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
package cn.orionsec.ops.handler.scheduler;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.scheduler.SchedulerTaskMachineStatus;
import cn.orionsec.ops.handler.scheduler.machine.ITaskMachineHandler;

import java.util.Collection;

/**
 * 并行任务处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 22:57
 */
public class ParallelTaskProcessor extends AbstractTaskProcessor {

    public ParallelTaskProcessor(Long recordId) {
        super(recordId);
    }

    @Override
    protected void handler() throws Exception {
        // 阻塞执行所有任务
        Collection<ITaskMachineHandler> handlers = this.handlers.values();
        Threads.blockRun(handlers, SchedulerPools.SCHEDULER_TASK_MACHINE_SCHEDULER);
        // 检查是否停止
        if (terminated) {
            return;
        }
        // 全部停止
        final boolean allTerminated = handlers.stream()
                .map(ITaskMachineHandler::getStatus)
                .allMatch(SchedulerTaskMachineStatus.TERMINATED::equals);
        if (allTerminated) {
            this.terminated = true;
            return;
        }
        // 全部成功
        final boolean allSuccess = handlers.stream()
                .map(ITaskMachineHandler::getStatus)
                .filter(s -> !SchedulerTaskMachineStatus.TERMINATED.equals(s))
                .allMatch(SchedulerTaskMachineStatus.SUCCESS::equals);
        if (!allSuccess) {
            throw Exceptions.log(MessageConst.OPERATOR_NOT_ALL_SUCCESS);
        }
    }

    @Override
    public void terminateAll() {
        super.terminateAll();
        handlers.values().forEach(ITaskMachineHandler::terminate);
    }

}
