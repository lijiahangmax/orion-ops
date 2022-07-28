package com.orion.ops.utils;

import com.orion.lang.exception.AuthenticationException;
import com.orion.lang.exception.ConnectionRuntimeException;
import com.orion.lang.exception.DisabledException;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Urls;
import com.orion.ops.constant.ws.WsCloseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

/**
 * websocket 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/14 0:36
 */
@Slf4j
public class WebSockets {

    /**
     * 机器id
     */
    public static final String MID = "mid";

    /**
     * 用户id
     */
    public static final String UID = "uid";

    /**
     * 是否已连接
     */
    public static final String CONNECTED = "connected";

    private WebSockets() {
    }

    /**
     * 发送消息 忽略并发报错
     *
     * @param session session
     * @param message message
     */
    public static void sendText(WebSocketSession session, byte[] message) {
        try {
            // 响应
            session.sendMessage(new TextMessage(message));
        } catch (IllegalStateException e) {
            // 并发异常
            log.error("terminal 读取流发生并发 {}", Exceptions.getDigest(e));
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        }
    }

    /**
     * 获取 urlToken
     *
     * @param request request
     * @return token
     */
    public static String getToken(ServerHttpRequest request) {
        return Urls.getUrlSource(Objects.requireNonNull(request.getURI().toString()));
    }

    /**
     * 获取 urlToken
     *
     * @param session session
     * @return token
     */
    public static String getToken(WebSocketSession session) {
        return Urls.getUrlSource(Objects.requireNonNull(session.getUri()).toString());
    }

    /**
     * 打开 session 异常关闭
     *
     * @param session session
     * @param e       e
     * @throws IOException IOException
     */
    public static void openSessionStoreThrowClose(WebSocketSession session, Exception e) throws IOException {
        if (e instanceof ConnectionRuntimeException) {
            session.close(WsCloseCode.CONNECTED_FAILURE.status());
        } else if (e instanceof AuthenticationException) {
            session.close(WsCloseCode.CONNECTED_AUTH_FAILURE.status());
        } else if (e instanceof DisabledException) {
            session.close(WsCloseCode.MACHINE_DISABLED.status());
        } else {
            session.close(WsCloseCode.CONNECTED_EXCEPTION.status());
        }
    }

}
