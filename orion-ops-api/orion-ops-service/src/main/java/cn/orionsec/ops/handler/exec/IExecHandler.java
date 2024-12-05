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
package cn.orionsec.ops.handler.exec;

import cn.orionsec.kit.lang.able.Executable;
import cn.orionsec.kit.lang.able.SafeCloseable;

/**
 * 命令执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 22:54
 */
public interface IExecHandler extends Runnable, Executable, SafeCloseable {

    /**
     * 写入
     *
     * @param out out
     */
    void write(String out);

    /**
     * 停止
     */
    void terminate();

    /**
     * 获取实际执行 handler
     *
     * @param execId execId
     * @return handler
     */
    static IExecHandler with(Long execId) {
        return new CommandExecHandler(execId);
    }

}