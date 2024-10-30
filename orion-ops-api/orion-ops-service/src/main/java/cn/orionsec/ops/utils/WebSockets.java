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
package cn.orionsec.ops.utils;

import cn.orionsec.ops.constant.terminal.TerminalClientOperate;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import com.orion.lang.define.wrapper.Tuple;
import com.orion.lang.exception.AuthenticationException;
import com.orion.lang.exception.ConnectionRuntimeException;
import com.orion.lang.exception.DisabledException;
import com.orion.lang.exception.TimeoutException;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Urls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.CloseStatus;
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

    public static final String CONFIG = "config";

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
        if (payload.length() < TerminalClientOperate.PREFIX_SIZE) {
            return null;
        }
        // 解析操作
        TerminalClientOperate operate = TerminalClientOperate.of(payload.substring(0, TerminalClientOperate.PREFIX_SIZE));
        if (operate == null) {
            return null;
        }
        if (!operate.isHasBody()) {
            return Tuple.of(operate, null);
        }
        // 检查是否有body
        if (payload.length() < TerminalClientOperate.PREFIX_SIZE + 1) {
            return null;
        }
        return Tuple.of(operate, payload.substring(TerminalClientOperate.PREFIX_SIZE + 1));
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
            session.close(new CloseStatus(code.getCode(), code.getReason()));
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
     */
    public static void openSessionStoreThrowClose(WebSocketSession session, Exception e) {
        if (Exceptions.isCausedBy(e, TimeoutException.class)) {
            close(session, WsCloseCode.CONNECTION_TIMEOUT);
        } else if (Exceptions.isCausedBy(e, ConnectionRuntimeException.class)) {
            close(session, WsCloseCode.CONNECTION_FAILURE);
        } else if (Exceptions.isCausedBy(e, AuthenticationException.class)) {
            close(session, WsCloseCode.CONNECTION_AUTH_FAILURE);
        } else if (Exceptions.isCausedBy(e, DisabledException.class)) {
            close(session, WsCloseCode.MACHINE_DISABLED);
        } else {
            close(session, WsCloseCode.CONNECTION_EXCEPTION);
        }
    }

}
