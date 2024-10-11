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
package com.orion.ops.handler.terminal;

import com.orion.lang.able.SafeCloseable;
import com.orion.ops.constant.terminal.TerminalClientOperate;
import com.orion.ops.entity.config.TerminalConnectConfig;
import com.orion.ops.handler.terminal.manager.TerminalManagementHandler;
import com.orion.ops.handler.terminal.watcher.ITerminalWatcherProcessor;

/**
 * 操作处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:56
 */
public interface IOperateHandler extends TerminalManagementHandler, SafeCloseable {

    /**
     * 建立连接
     */
    void connect();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 处理消息
     *
     * @param operate 操作
     * @param body    body
     * @throws Exception ex
     */
    void handleMessage(TerminalClientOperate operate, String body) throws Exception;

    /**
     * 心跳是否结束
     *
     * @return true结束
     */
    boolean isDown();

    /**
     * 获取token
     *
     * @return token
     */
    String getToken();

    /**
     * 获取终端配置
     *
     * @return 终端配置
     */
    TerminalConnectConfig getHint();

    /**
     * 获取监视器
     *
     * @return processor
     */
    ITerminalWatcherProcessor getWatcher();

}
