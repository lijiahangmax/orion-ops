package com.orion.ops.handler.terminal;

import com.orion.lang.define.wrapper.Tuple;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.constant.terminal.TerminalOperate;
import com.orion.ops.constant.ws.WsCloseCode;
import com.orion.ops.constant.ws.WsProtocol;
import com.orion.ops.entity.config.TerminalConnectConfig;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.terminal.TerminalConnectDTO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * terminal message 处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/24 17:54
 */
@Slf4j
@Component("terminalMessageHandler")
public class TerminalMessageHandler implements WebSocketHandler {

    @Resource
    private TerminalSessionManager terminalSessionManager;

    @Resource
    private PassportService passportService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineTerminalService machineTerminalService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("terminal 已建立连接 token: {}, id: {}, params: {}", WebSockets.getToken(session), session.getId(), session.getAttributes());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
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
                if (session.getAttributes().get(WebSockets.CONNECTED) != null) {
                    return;
                }
                this.connect(session, token, body);
                return;
            }
            // 检查连接
            if (session.getAttributes().get(WebSockets.CONNECTED) == null) {
                WebSockets.close(session, WsCloseCode.VALID);
                return;
            }
            // 获取连接
            IOperateHandler handler = terminalSessionManager.getSession(token);
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
        log.error("terminal 操作异常拦截 token: {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = session.getId();
        int code = status.getCode();
        log.info("terminal 关闭连接 token: {}, code: {}, reason: {}", token, code, status.getReason());
        // 释放资源
        IOperateHandler handler = terminalSessionManager.removeSession(token);
        if (handler == null) {
            return;
        }
        handler.close();
        // 修改日志
        MachineTerminalLogDO updateLog = new MachineTerminalLogDO();
        updateLog.setCloseCode(code);
        updateLog.setDisconnectedTime(new Date());
        Integer effect = machineTerminalService.updateAccessLog(token, updateLog);
        log.info("terminal 连接关闭更新日志 token: {}, effect: {}", token, effect);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 建立连接
     *
     * @param session session
     * @param token   token
     * @param body    body
     */
    private void connect(WebSocketSession session, String token, String body) {
        log.info("terminal 尝试建立连接 token: {}, body: {}", token, body);
        // 检查参数
        TerminalConnectDTO connectInfo = TerminalUtils.parseConnectBody(body);
        if (connectInfo == null) {
            WebSockets.sendText(session, WsProtocol.ERROR.get());
            return;
        }
        Long userId = (Long) session.getAttributes().get(WebSockets.UID);
        Long machineId = (Long) session.getAttributes().get(WebSockets.MID);

        // 获取登陆用户
        UserDTO userDTO = passportService.getUserByToken(connectInfo.getLoginToken(), null);
        if (userDTO == null || !userId.equals(userDTO.getId())) {
            log.info("terminal 建立连接拒绝-用户认证失败 token: {}", token);
            WebSockets.close(session, WsCloseCode.IDENTITY_MISMATCH);
            return;
        }
        // 获取机器信息
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        if (machine == null) {
            log.info("terminal 建立连接拒绝-未查询到机器信息 token: {}, machineId: {}", token, machineId);
            WebSockets.close(session, WsCloseCode.INVALID_MACHINE);
            return;
        }
        session.getAttributes().put(WebSockets.CONNECTED, 1);
        // 建立连接
        SessionStore sessionStore;
        try {
            // 打开session
            sessionStore = machineInfoService.openSessionStore(machine);
            WebSockets.sendText(session, WsProtocol.CONNECTED.get());
        } catch (Exception e) {
            WebSockets.openSessionStoreThrowClose(session, e);
            log.error("terminal 建立连接失败-连接远程服务器失败 uid: {}, machineId: {}", userId, machineId, e);
            return;
        }

        // 配置
        TerminalConnectConfig hint = new TerminalConnectConfig();
        String terminalType = machineTerminalService.getMachineConfig(machineId).getTerminalType();
        hint.setUserId(userId);
        hint.setUsername(userDTO.getUsername());
        hint.setMachineId(machineId);
        hint.setMachineName(machine.getMachineName());
        hint.setMachineHost(machine.getMachineHost());
        hint.setMachineTag(machine.getMachineTag());
        hint.setCols(connectInfo.getCols());
        hint.setRows(connectInfo.getRows());
        hint.setTerminalType(terminalType);
        TerminalOperateHandler terminalHandler = new TerminalOperateHandler(token, hint, session, sessionStore);
        try {
            // 打开shell
            log.info("terminal 尝试建立连接-尝试打开shell token: {}", token);
            terminalHandler.connect();
            log.info("terminal 建立连接成功-打开shell成功 token: {}", token);
        } catch (Exception e) {
            WebSockets.close(session, WsCloseCode.OPEN_SHELL_EXCEPTION);
            log.error("terminal 建立连接失败-打开shell失败 machineId: {}, uid: {}", machineId, userId, e);
            return;
        }
        terminalSessionManager.addSession(token, terminalHandler);
        log.info("terminal 建立连接成功 uid: {}, machineId: {}", userId, machineId);
    }

}
