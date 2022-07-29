package com.orion.ops.utils;

import com.orion.lang.define.wrapper.Tuple;
import com.orion.lang.exception.AuthenticationException;
import com.orion.lang.exception.ConnectionRuntimeException;
import com.orion.lang.exception.DisabledException;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Urls;
import com.orion.ops.constant.terminal.TerminalOperate;
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

    public static final String MID = "mid";

    public static final String UID = "uid";

    public static final String TOKEN = "token";

    public static final String READONLY = "readonly";

    public static final String CONNECTED = "connected";

    public static final String AUTHED = "authed";

    private WebSockets() {
    }

    /**
     * 解析请求
     * <p>
     * .e.g xx
     * .e.g xx|body
     *
     * @param payload payload
     * @return operator, body
     */
    public static Tuple parsePayload(String payload) {
        // 检查长度
        if (payload.length() < TerminalOperate.PREFIX_SIZE) {
            return null;
        }
        // 解析操作
        TerminalOperate operate = TerminalOperate.of(payload.substring(0, TerminalOperate.PREFIX_SIZE));
        if (operate == null) {
            return null;
        }
        if (!operate.isHasBody()) {
            return Tuple.of(operate, null);
        }
        // 检查是否有body
        if (payload.length() < TerminalOperate.PREFIX_SIZE + 1) {
            return null;
        }
        return Tuple.of(operate, payload.substring(TerminalOperate.PREFIX_SIZE + 1));
    }

    /**
     * 发送消息 忽略并发报错
     *
     * @param session session
     * @param message message
     */
    public static void sendText(WebSocketSession session, byte[] message) {
        if (!session.isOpen()) {
            return;
        }
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
     * 关闭会话
     *
     * @param session session
     * @param code    code
     */
    public static void close(WebSocketSession session, WsCloseCode code) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.close(code.status());
        } catch (Exception e) {
            log.error("websocket close failure", e);
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
