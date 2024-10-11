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
package com.orion.ops.handler.app.release;

import com.orion.ops.constant.app.ActionStatus;
import com.orion.ops.constant.common.ExceptionHandlerType;
import com.orion.ops.handler.app.machine.IMachineProcessor;
import com.orion.ops.handler.app.machine.ReleaseMachineProcessor;

import java.util.Collection;

/**
 * 串行处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 15:33
 */
public class SerialReleaseProcessor extends AbstractReleaseProcessor {

    public SerialReleaseProcessor(Long releaseId) {
        super(releaseId);
    }

    @Override
    protected void handler() throws Exception {
        // 异常处理策略
        final boolean errorSkipAll = ExceptionHandlerType.SKIP_ALL.getType().equals(release.getExceptionHandler());
        Exception ex = null;
        Collection<ReleaseMachineProcessor> processors = machineProcessors.values();
        for (ReleaseMachineProcessor processor : processors) {
            // 停止则跳过
            if (terminated) {
                processor.skip();
                continue;
            }
            // 发生异常并且异常处理策略是跳过所有则跳过
            if (ex != null && errorSkipAll) {
                processor.skip();
                continue;
            }
            // 执行
            try {
                processor.run();
            } catch (Exception e) {
                ex = e;
            }
        }
        // 异常返回
        if (ex != null) {
            throw ex;
        }
        // 全部停止
        final boolean allTerminated = processors.stream()
                .map(ReleaseMachineProcessor::getStatus)
                .filter(s -> !ActionStatus.SKIPPED.equals(s))
                .allMatch(ActionStatus.TERMINATED::equals);
        if (allTerminated) {
            this.terminated = true;
        }
    }

    @Override
    public void terminateAll() {
        super.terminateAll();
        // 获取当前执行中的机器执行器
        machineProcessors.values().stream()
                .filter(s -> s.getStatus().equals(ActionStatus.RUNNABLE))
                .forEach(IMachineProcessor::terminate);
    }

}
