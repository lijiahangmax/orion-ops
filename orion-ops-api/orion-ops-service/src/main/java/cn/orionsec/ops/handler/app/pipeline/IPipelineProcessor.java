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
package cn.orionsec.ops.handler.app.pipeline;

import com.orion.lang.able.Executable;

/**
 * 流水线处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 14:18
 */
public interface IPipelineProcessor extends Executable, Runnable {

    /**
     * 获取明细id
     *
     * @return taskId
     */
    Long getTaskId();

    /**
     * 停止执行
     */
    void terminate();

    /**
     * 停止执行详情
     *
     * @param id id
     */
    void terminateDetail(Long id);

    /**
     * 跳过执行详情
     *
     * @param id id
     */
    void skipDetail(Long id);

    /**
     * 获取流水线执行器
     *
     * @param id id
     * @return 流水线执行器
     */
    static IPipelineProcessor with(Long id) {
        return new PipelineProcessor(id);
    }

}
