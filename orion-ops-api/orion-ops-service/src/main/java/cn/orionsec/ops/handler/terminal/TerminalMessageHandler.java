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
package cn.orionsec.ops.handler.terminal;

import cn.orionsec.ops.constant.terminal.TerminalClientOperate;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.constant.ws.WsProtocol;
import cn.orionsec.ops.entity.config.TerminalConnectConfig;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.dto.terminal.TerminalConnectDTO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.handler.terminal.manager.TerminalSessionManager;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.MachineTerminalService;
import cn.orionsec.ops.service.api.PassportService;
import cn.orionsec.ops.utils.WebSockets;
import com.orion.lang.define.wrapper.Tuple;
import com.orion.net.remote.channel.SessionStore;
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
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (!(message instanceof TextMessage)) {
            return;
        }
        String token = session.getId();
        try {
            // 解析请求
            Tuple tuple = WebSockets.parsePayload(((TextMessage) message).getPayload());
            if (tuple == null) {
                WebSockets.sendText(session, WsProtocol.ERROR.get());
                return;
            }
            TerminalClientOperate operate = tuple.get(0);
            String body = tuple.get(1);

            // 建立连接
            if (operate == TerminalClientOperate.CONNECT) {
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
            log.error("terminal 处理操作异常 token: {}", token, e);
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

        // 获取登录用户
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
