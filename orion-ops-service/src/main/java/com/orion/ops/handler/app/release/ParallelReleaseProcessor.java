package com.orion.ops.handler.app.release;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Threads;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.app.machine.ReleaseMachineProcessor;

import java.util.Collection;

/**
 * 并行处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 15:32
 */
public class ParallelReleaseProcessor extends AbstractReleaseProcessor {

    public ParallelReleaseProcessor(Long releaseId) {
        super(releaseId);
    }

    @Override
    protected void handler() throws Exception {
        Collection<ReleaseMachineProcessor> processor = machineProcessors.values();
        Threads.blockRun(processor, SchedulerPools.RELEASE_MACHINE_SCHEDULER);
        // 检查是否停止
        if (terminated) {
            return;
        }
        // 全部停止
        final boolean allTerminated = processor.stream()
                .map(ReleaseMachineProcessor::getStatus)
                .allMatch(ActionStatus.TERMINATED::equals);
        if (allTerminated) {
            this.terminated = true;
            return;
        }
        // 全部完成
        boolean allFinish = processor.stream()
                .map(ReleaseMachineProcessor::getStatus)
                .filter(s -> !ActionStatus.TERMINATED.equals(s))
                .allMatch(ActionStatus.FINISH::equals);
        if (!allFinish) {
            throw Exceptions.log(MessageConst.OPERATOR_NOT_ALL_SUCCESS);
        }
    }

    @Override
    public void terminateAll() {
        super.terminateAll();
        machineProcessors.values().forEach(ReleaseMachineProcessor::terminate);
    }

}
