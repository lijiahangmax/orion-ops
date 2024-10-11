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
package com.orion.ops.handler.tail;

import com.orion.lang.able.SafeCloseable;
import com.orion.lang.function.select.Branches;
import com.orion.lang.function.select.Selector;
import com.orion.ops.constant.tail.FileTailMode;
import com.orion.ops.handler.tail.impl.ExecTailFileHandler;
import com.orion.ops.handler.tail.impl.TrackerTailFileHandler;
import org.springframework.web.socket.WebSocketSession;

/**
 * tail 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:05
 */
public interface ITailHandler extends SafeCloseable {

    /**
     * 开始
     *
     * @throws Exception Exception
     */
    void start() throws Exception;

    /**
     * 获取机器id
     *
     * @return 机器id
     */
    Long getMachineId();

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    String getFilePath();

    /**
     * 设置最后修改时间
     */
    default void setLastModify() {
    }

    /**
     * 写入命令
     *
     * @param command command
     */
    default void write(String command) {
    }

    /**
     * 获取实际执行 handler
     *
     * @param mode    mode
     * @param hint    hint
     * @param session session
     * @return handler
     */
    static ITailHandler with(FileTailMode mode, TailFileHint hint, WebSocketSession session) {
        return Selector.<FileTailMode, ITailHandler>of(mode)
                .test(Branches.eq(FileTailMode.TRACKER)
                        .then(() -> new TrackerTailFileHandler(hint, session)))
                .test(Branches.eq(FileTailMode.TAIL)
                        .then(() -> new ExecTailFileHandler(hint, session)))
                .get();
    }

}
