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
package cn.orionsec.ops.handler.terminal.manager;

/**
 * 管理handler
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:21
 */
public interface TerminalManagementHandler {

    /**
     * 管理员强制下线
     *
     * @throws Exception Exception
     */
    void forcedOffline() throws Exception;

    /**
     * 心跳结束下线
     *
     * @throws Exception Exception
     */
    void heartbeatDownClose() throws Exception;

    /**
     * 主动发送心跳
     */
    void sendHeartbeat();

}
