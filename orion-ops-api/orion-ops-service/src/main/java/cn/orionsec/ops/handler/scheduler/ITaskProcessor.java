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
package cn.orionsec.ops.handler.scheduler;

import cn.orionsec.kit.lang.able.SafeCloseable;
import cn.orionsec.kit.lang.function.select.Branches;
import cn.orionsec.kit.lang.function.select.Selector;
import cn.orionsec.ops.constant.common.SerialType;

/**
 * 任务处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 20:59
 */
public interface ITaskProcessor extends Runnable, SafeCloseable {

    /**
     * 停止全部
     */
    void terminateAll();

    /**
     * 停止机器操作
     *
     * @param recordMachineId recordMachineId
     */
    void terminateMachine(Long recordMachineId);

    /**
     * 跳过机器操作
     *
     * @param recordMachineId recordMachineId
     */
    void skipMachine(Long recordMachineId);

    /**
     * 发送机器命令
     *
     * @param recordMachineId recordMachineId
     * @param command         command
     */
    void writeMachine(Long recordMachineId, String command);

    /**
     * 获取实际执行处理器
     *
     * @param recordId recordId
     * @param type     type
     * @return 处理器
     */
    static ITaskProcessor with(Long recordId, SerialType type) {
        return Selector.<SerialType, ITaskProcessor>of(type)
                .test(Branches.eq(SerialType.SERIAL)
                        .then(() -> new SerialTaskProcessor(recordId)))
                .test(Branches.eq(SerialType.PARALLEL)
                        .then(() -> new ParallelTaskProcessor(recordId)))
                .get();
    }

}
