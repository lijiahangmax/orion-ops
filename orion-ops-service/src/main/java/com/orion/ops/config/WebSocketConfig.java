package com.orion.ops.config;

import com.orion.ops.handler.terminal.TerminalMessageHandler;
import com.orion.ops.interceptor.TerminalAccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

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

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(terminalMessageHandler, "/machine/terminal/{accessToken}")
                .addInterceptors(terminalAccessInterceptor)
                .setAllowedOrigins("*");
    }

}
