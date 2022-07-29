package com.orion.ops.handler.terminal.watcher;

import com.orion.lang.define.wrapper.Tuple;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.terminal.TerminalOperate;
import com.orion.ops.constant.ws.WsCloseCode;
import com.orion.ops.constant.ws.WsProtocol;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.handler.terminal.IOperateHandler;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * terminal watcher 处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 11:21
 */
@Slf4j
@Component("terminalWatcherHandler")
public class TerminalWatcherHandler implements WebSocketHandler {

    @Resource
    private TerminalSessionManager terminalSessionManager;

    @Resource
    private PassportService passportService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("terminal-watcher 已建立连接 token: {}, id: {}, params: {}", WebSockets.getToken(session), session.getId(), session.getAttributes());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (!(message instanceof TextMessage)) {
            return;
        }
        String token = session.getId();
        String payload = ((TextMessage) message).getPayload();
        try {
            // 解析请求
            Tuple tuple = WebSockets.parsePayload(payload);
            if (tuple == null) {
                WebSockets.sendText(session, WsProtocol.ERROR.get());
                return;
            }
            TerminalOperate operate = tuple.get(0);
            String body = tuple.get(1);

            // 建立连接
            if (operate == TerminalOperate.CONNECT) {
                // 建立连接
                if (session.getAttributes().get(WebSockets.AUTHED) != null) {
                    return;
                }
                this.auth(session, body);
                return;
            }
            if (operate != TerminalOperate.KEY || operate != TerminalOperate.NOP) {
                return;
            }
            // 检查连接
            if (session.getAttributes().get(WebSockets.AUTHED) == null) {
                WebSockets.close(session, WsCloseCode.VALID);
                return;
            }
            // 检查是否只读
            if (Const.ENABLE.equals(session.getAttributes().get(WebSockets.READONLY))) {
                return;
            }
            // 获取连接
            String terminalToken = session.getAttributes().get(WebSockets.TOKEN).toString();
            IOperateHandler handler = terminalSessionManager.getSession(terminalToken);
            if (handler == null) {
                WebSockets.close(session, WsCloseCode.UNKNOWN_CONNECT);
                return;
            }
            // 操作
            handler.handleMessage(operate, body);
        } catch (Exception e) {
            log.error("terminal 处理操作异常 token: {}, payload: {}", token, payload, e);
            WebSockets.close(session, WsCloseCode.RUNTIME_EXCEPTION);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("terminal-watcher 操作异常拦截 token: {}", session.getId(), exception);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("terminal-watcher 关闭连接 token: {}, code: {}, reason: {}", session.getId(), status.getCode(), status.getReason());
        // 这时候主可能已经关了
        IOperateHandler handler = terminalSessionManager.getSession((String) session.getAttributes().get(WebSockets.TOKEN));
        if (handler == null) {
            return;
        }
        handler.getWatcher().removeWatcher(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 认证
     *
     * @param session    session
     * @param loginToken loginToken
     * @throws IOException IOException
     */
    private void auth(WebSocketSession session, String loginToken) throws IOException {
        // 检查参数
        Long userId = (Long) session.getAttributes().get(WebSockets.UID);
        // 获取登陆用户
        UserDTO userDTO = passportService.getUserByToken(loginToken, null);
        if (userDTO == null || !userId.equals(userDTO.getId())) {
            WebSockets.close(session, WsCloseCode.IDENTITY_MISMATCH);
            return;
        }
        session.getAttributes().put(WebSockets.AUTHED, 1);
        // 获取连接
        String terminalToken = session.getAttributes().get(WebSockets.TOKEN).toString();
        IOperateHandler handler = terminalSessionManager.getSession(terminalToken);
        // 设置 watcher
        handler.getWatcher().addWatcher(session);
        WebSockets.sendText(session, WsProtocol.CONNECTED.get());
    }

}
