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
package cn.orionsec.ops.config;

import cn.orionsec.ops.handler.sftp.notify.FileTransferNotifyHandler;
import cn.orionsec.ops.handler.tail.TailFileHandler;
import cn.orionsec.ops.handler.terminal.TerminalMessageHandler;
import cn.orionsec.ops.handler.terminal.watcher.TerminalWatcherHandler;
import cn.orionsec.ops.interceptor.FileTransferNotifyInterceptor;
import cn.orionsec.ops.interceptor.TailFileInterceptor;
import cn.orionsec.ops.interceptor.TerminalAccessInterceptor;
import cn.orionsec.ops.interceptor.TerminalWatcherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.annotation.Resource;

/**
 * webSocket 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/24 17:52
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private TerminalMessageHandler terminalMessageHandler;

    @Resource
    private TerminalAccessInterceptor terminalAccessInterceptor;

    @Resource
    private TerminalWatcherHandler terminalWatcherHandler;

    @Resource
    private TerminalWatcherInterceptor terminalWatcherInterceptor;

    @Resource
    private TailFileHandler tailFileHandler;

    @Resource
    private TailFileInterceptor tailFileInterceptor;

    @Resource
    private FileTransferNotifyHandler fileTransferNotifyHandler;

    @Resource
    private FileTransferNotifyInterceptor fileTransferNotifyInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(terminalMessageHandler, "/orion/keep-alive/machine/terminal/{token}")
                .addInterceptors(terminalAccessInterceptor)
                .setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(terminalWatcherHandler, "/orion/keep-alive/watcher/terminal/{token}")
                .addInterceptors(terminalWatcherInterceptor)
                .setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(tailFileHandler, "/orion/keep-alive/tail/{token}")
                .addInterceptors(tailFileInterceptor)
                .setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(fileTransferNotifyHandler, "/orion/keep-alive/sftp/notify/{token}")
                .addInterceptors(fileTransferNotifyInterceptor)
                .setAllowedOrigins("*");
    }

    /**
     * web socket 缓冲区大小配置
     */
    @Bean
    public ServletServerContainerFactoryBean servletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean factory = new ServletServerContainerFactoryBean();
        factory.setMaxBinaryMessageBufferSize(1024 * 1024);
        factory.setMaxTextMessageBufferSize(1024 * 1024);
        factory.setMaxSessionIdleTimeout(30 * 60000L);
        return factory;
    }

}
