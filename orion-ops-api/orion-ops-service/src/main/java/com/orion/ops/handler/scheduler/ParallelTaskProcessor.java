package com.orion.ops.handler.scheduler;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Threads;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.scheduler.SchedulerTaskMachineStatus;
import com.orion.ops.handler.scheduler.machine.ITaskMachineHandler;

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
