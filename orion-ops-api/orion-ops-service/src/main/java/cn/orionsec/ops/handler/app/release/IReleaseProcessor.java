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
package cn.orionsec.ops.handler.app.release;

import cn.orionsec.ops.constant.common.SerialType;
import cn.orionsec.ops.entity.domain.ApplicationReleaseDO;
import com.orion.lang.able.Executable;
import com.orion.lang.able.SafeCloseable;
import com.orion.lang.function.select.Branches;
import com.orion.lang.function.select.Selector;

/**
 * 发布处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:12
 */
public interface IReleaseProcessor extends Executable, Runnable, SafeCloseable {

    /**
     * 获取id
     *
     * @return id
     */
    Long getReleaseId();

    /**
     * 终止
     */
    void terminateAll();

    /**
     * 终止机器操作
     *
     * @param releaseMachineId releaseMachineId
     */
    void terminateMachine(Long releaseMachineId);

    /**
     * 跳过机器操作
     *
     * @param releaseMachineId 机器id
     */
    void skipMachine(Long releaseMachineId);

    /**
     * 输入机器命令
     *
     * @param releaseMachineId 机器id
     * @param command          command
     */
    void writeMachine(Long releaseMachineId, String command);

    /**
     * 获取发布执行器
     *
     * @param release release
     * @return 执行器
     */
    static IReleaseProcessor with(ApplicationReleaseDO release) {
        return Selector.<SerialType, IReleaseProcessor>of(SerialType.of(release.getReleaseSerialize()))
                .test(Branches.eq(SerialType.SERIAL)
                        .then(() -> new SerialReleaseProcessor(release.getId())))
                .test(Branches.eq(SerialType.PARALLEL)
                        .then(() -> new ParallelReleaseProcessor(release.getId())))
                .get();
    }

}
