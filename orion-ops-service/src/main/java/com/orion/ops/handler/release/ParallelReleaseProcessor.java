package com.orion.ops.handler.release;

import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.release.machine.IMachineProcessor;
import com.orion.utils.Exceptions;
import com.orion.utils.Threads;
import com.orion.utils.collect.Lists;

import java.util.List;

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
        List<IMachineProcessor> processor = Lists.newList(store.getMachineProcessors().values());
        Threads.blockRun(processor, SchedulerPools.RELEASE_MACHINE_SCHEDULER);
        // 检查是否成功
        if (terminated) {
            return;
        }
        if (!processor.stream().map(IMachineProcessor::getStatus).allMatch(ActionStatus.FINISH::equals)) {
            throw Exceptions.log(MessageConst.RELEASE_NOT_ALL_SUCCESS);
        }
    }

    @Override
    public void terminated() {
        super.terminated();
        store.getMachineProcessors().values().forEach(IMachineProcessor::terminated);
    }

}
