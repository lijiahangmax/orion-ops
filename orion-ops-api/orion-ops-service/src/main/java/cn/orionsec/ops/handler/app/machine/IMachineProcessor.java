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
package cn.orionsec.ops.handler.app.machine;

import cn.orionsec.kit.lang.able.SafeCloseable;

/**
 * 机器执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 13:38
 */
public interface IMachineProcessor extends Runnable, SafeCloseable {

    /**
     * 终止
     */
    void terminate();

    /**
     * 输入命令
     *
     * @param command command
     */
    void write(String command);

    /**
     * 跳过
     */
    default void skip() {
    }

}
