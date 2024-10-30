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

import cn.orionsec.ops.constant.common.ExceptionHandlerType;
import cn.orionsec.ops.constant.scheduler.SchedulerTaskMachineStatus;
import cn.orionsec.ops.handler.scheduler.machine.ITaskMachineHandler;

import java.util.Collection;

/**
 * 串行任务处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 22:58
 */
public class SerialTaskProcessor extends AbstractTaskProcessor {

    public SerialTaskProcessor(Long recordId) {
        super(recordId);
    }

    @Override
    protected void handler() throws Exception {
        // 异常处理策略
        final boolean errorSkipAll = ExceptionHandlerType.SKIP_ALL.getType().equals(task.getExceptionHandler());
        // 串行执行
        Exception ex = null;
        Collection<ITaskMachineHandler> handlers = this.handlers.values();
        for (ITaskMachineHandler handler : handlers) {
            // 停止跳过
            if (terminated) {
                handler.skip();
                continue;
            }
            // 发生异常并且异常处理策略是跳过所有则跳过
            if (ex != null && errorSkipAll) {
                handler.skip();
                continue;
            }
            // 执行
            try {
                handler.run();
            } catch (Exception e) {
                ex = e;
            }
        }
        // 异常返回
        if (ex != null) {
            throw ex;
        }
        // 全部停止
        final boolean allTerminated = handlers.stream()
                .map(ITaskMachineHandler::getStatus)
                .filter(s -> !SchedulerTaskMachineStatus.SKIPPED.equals(s))
                .allMatch(SchedulerTaskMachineStatus.TERMINATED::equals);
        if (allTerminated) {
            this.terminated = true;
        }
    }

    @Override
    public void terminateAll() {
        super.terminateAll();
        // 获取当前执行中的机器执行器
        handlers.values().stream()
                .filter(s -> s.getStatus().equals(SchedulerTaskMachineStatus.RUNNABLE))
                .forEach(ITaskMachineHandler::terminate);
    }

}
