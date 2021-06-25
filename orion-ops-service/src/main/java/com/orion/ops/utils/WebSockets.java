package com.orion.ops.utils;

import com.orion.exception.AuthenticationException;
import com.orion.exception.ConnectionRuntimeException;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.utils.Urls;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/14 0:36
 */
public class WebSockets {

    private WebSockets() {
    }

    /**
     * 获取urlToken
     *
     * @param request request
     * @return token
     */
    public static String getToken(ServerHttpRequest request) {
        return Urls.getUrlSource(Objects.requireNonNull(request.getURI().toString()));
    }

    /**
     * 获取urlToken
     *
     * @param session session
     * @return token
     */
    public static String getToken(WebSocketSession session) {
        return Urls.getUrlSource(Objects.requireNonNull(session.getUri()).toString());
    }

    /**
     * 打开session 异常关闭
     *
     * @param session session
     * @param e       e
     * @throws IOException IOException
     */
    public static void openSessionStoreThrowClose(WebSocketSession session, Exception e) throws IOException {
        if (e instanceof ConnectionRuntimeException) {
            session.close(WsCloseCode.CONNECTED_FAILURE.close());
        } else if (e instanceof AuthenticationException) {
            session.close(WsCloseCode.CONNECTED_AUTH_FAILURE.close());
        } else {
            session.close(WsCloseCode.CONNECTED_EXCEPTION.close());
        }
    }

}
