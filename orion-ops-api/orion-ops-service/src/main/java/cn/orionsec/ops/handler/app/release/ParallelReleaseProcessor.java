/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.handler.app.release;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.app.ActionStatus;
import cn.orionsec.ops.handler.app.machine.ReleaseMachineProcessor;

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
