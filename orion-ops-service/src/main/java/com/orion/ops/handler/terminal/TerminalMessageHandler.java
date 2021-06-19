package com.orion.ops.handler.terminal;

import com.alibaba.fastjson.JSON;
import com.orion.exception.AuthenticationException;
import com.orion.exception.ConnectionRuntimeException;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.machine.MachineConst;
import com.orion.ops.consts.terminal.TerminalOperate;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.consts.ws.WsProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalConnectDTO;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.service.api.PassportService;
import com.orion.remote.channel.SessionStore;
import com.orion.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.orion.ops.utils.WebSockets.getToken;

/**
 * webSocket 处理器
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
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private PassportService passportService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineTerminalService machineTerminalService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = session.getId();
        String token = getToken(session);
        // 检查伪造token
        Long tokenUserId = MachineTerminalService.getTokenUserId(token);
        if (tokenUserId == null) {
            session.close(WsCloseCode.FORGE_TOKEN.close());
            return;
        }
        // 检查token
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        // 未查询到token
        if (tokenValue == null) {
            session.close(WsCloseCode.INCORRECT_TOKEN.close());
            return;
        }
        // 检查bind
        String bindKey = Strings.format(KeyConst.TERMINAL_BIND, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (bindValue != null) {
            session.close(WsCloseCode.TOKEN_BIND.close());
            return;
        }
        // token绑定
        redisTemplate.opsForValue().set(bindKey, id, KeyConst.TERMINAL_BIND_EXPIRE, TimeUnit.SECONDS);
        // 刷新token 过期时间
        redisTemplate.expire(tokenKey, KeyConst.TERMINAL_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        session.sendMessage(new TextMessage(WsProtocol.ACK.get()));
        log.info("terminal 建立ws连接 token: {}, id: {}", token, id);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String token = getToken(session);
        String id = session.getId();
        TerminalDataTransferDTO data = null;
        boolean valid = false;
        try {
            if (!(message instanceof TextMessage)) {
                return;
            }
            byte[] body = ((TextMessage) message).asBytes();
            try {
                data = JSON.parseObject(new String(body), TerminalDataTransferDTO.class);
            } catch (Exception e) {
                // ignore
            }
            if (data == null) {
                session.sendMessage(new TextMessage(WsProtocol.ILLEGAL_BODY.get()));
                return;
            }
            // 操作
            TerminalOperate operate = TerminalOperate.of(data.getOperate());
            if (operate == null) {
                session.sendMessage(new TextMessage(WsProtocol.UNKNOWN_OPERATE.get()));
                return;
            }
            IOperateHandler handler;
            if (operate == TerminalOperate.CONNECT) {
                // 建立连接
                handler = this.connect(session, id, data, token);
                if (handler == null) {
                    return;
                }
            } else {
                // 获取
                handler = terminalSessionManager.getSessionStore().get(token);
            }
            // 未找到连接
            if (handler == null) {
                session.close(WsCloseCode.UNKNOWN_CONNECT.close());
                return;
            }
            // 认证
            valid = handler.valid(id);
            if (!valid) {
                session.close(WsCloseCode.VALID.close());
                return;
            }
            // 操作
            handler.handleMessage(data, operate);
        } catch (Exception e) {
            log.error("terminal 处理操作异常 token: {}, data: {}, e: {}", token, data, e);
            e.printStackTrace();
            if (valid) {
                session.close(WsCloseCode.RUNTIME_VALID_EXCEPTION.close());
            } else {
                session.close(WsCloseCode.RUNTIME_EXCEPTION.close());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String token = getToken(session);
        log.error("terminal 操作异常拦截 token: {}, e: {}", token, exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String id = session.getId();
        int code = status.getCode();
        String token = getToken(session);
        String accessKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String bindKey = Strings.format(KeyConst.TERMINAL_BIND, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (!id.equals(bindValue)) {
            return;
        }
        log.info("terminal 关闭连接 token: {}, id:{}, code: {}, reason: {}", token, id, code, status.getReason());
        redisTemplate.delete(accessKey);
        redisTemplate.delete(bindKey);
        // 释放资源
        IOperateHandler handler = terminalSessionManager.getSessionStore().get(token);
        if (handler == null) {
            return;
        }
        handler.disconnect();
        terminalSessionManager.getSessionStore().remove(token);
        // log
        MachineTerminalLogDO updateLog = new MachineTerminalLogDO();
        updateLog.setCloseCode(code);
        updateLog.setDisconnectedTime(new Date());
        Integer logUpdateEffect = machineTerminalService.updateAccessLog(token, updateLog);
        log.info("terminal 连接关闭更新日志 token: {}, id: {}, effect: {}", token, id, logUpdateEffect);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 建立连接
     *
     * @param session session
     * @param data    data
     * @return handler
     */
    private TerminalOperateHandler connect(WebSocketSession session, String id, TerminalDataTransferDTO data, String token) throws IOException {
        log.info("terminal 尝试建立连接 token: {}, id: {}, data: {}", token, id, JSON.toJSONString(data));
        // 检查参数
        TerminalConnectDTO connectInfo = null;
        try {
            connectInfo = JSON.parseObject(data.getBody(), TerminalConnectDTO.class);
        } catch (Exception e) {
            // ignore
        }
        if (connectInfo == null) {
            session.sendMessage(new TextMessage(WsProtocol.ARGUMENT.get()));
            return null;
        }
        String loginToken = connectInfo.getLoginToken();
        if (Strings.isBlank(loginToken) ||
                connectInfo.getRows() == null || connectInfo.getCols() == null ||
                connectInfo.getWidth() == null || connectInfo.getHeight() == null) {
            session.sendMessage(new TextMessage(WsProtocol.ARGUMENT.get()));
            return null;
        }
        // 检查伪造token
        Long tokenUserId = MachineTerminalService.getTokenUserId(token);
        if (tokenUserId == null) {
            log.info("terminal 建立连接驳回-伪造token token: {}", token);
            session.close(WsCloseCode.FORGE_TOKEN.close());
            return null;
        }
        // 检查token
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        if (tokenValue == null) {
            log.info("terminal 建立连接驳回-token不存在 token: {}", token);
            session.close(WsCloseCode.INCORRECT_TOKEN.close());
            return null;
        }
        // 检查id
        String idKey = Strings.format(KeyConst.TERMINAL_BIND, token);
        String idValue = redisTemplate.opsForValue().get(idKey);
        if (idValue == null || !idValue.equals(id)) {
            log.info("terminal 建立连接驳回-id认证失败 token: {}", token);
            session.close(WsCloseCode.IDENTITY_MISMATCH.close());
            return null;
        }
        // 检查操作用户
        UserDTO userDTO = passportService.getUserByToken(loginToken);
        if (userDTO == null || !tokenUserId.equals(userDTO.getId())) {
            log.info("terminal 建立连接驳回-用户认证失败 token: {}", token);
            session.close(WsCloseCode.IDENTITY_MISMATCH.close());
            return null;
        }
        // 删除token
        redisTemplate.delete(tokenKey);
        // 建立连接
        Long machineId = Long.valueOf(tokenValue);
        SessionStore sessionStore;
        try {
            // 打开session
            sessionStore = machineInfoService.openSessionStore(machineId);
        } catch (Exception e) {
            if (e instanceof ConnectionRuntimeException) {
                session.close(WsCloseCode.CONNECTED_FAILURE.close());
            } else if (e instanceof AuthenticationException) {
                session.close(WsCloseCode.CONNECTED_AUTH_FAILURE.close());
            } else {
                session.close(WsCloseCode.CONNECTED_EXCEPTION.close());
            }
            log.error("terminal 建立连接失败-连接远程服务器失败 uid: {}, {}", tokenUserId, e);
            return null;
        }
        // 配置
        String host = sessionStore.getHost();
        TerminalConnectHint config = new TerminalConnectHint();
        String terminalType = machineTerminalService.getMachineConfig(machineId).getTerminalType();
        config.setUserId(tokenUserId);
        config.setUsername(userDTO.getUsername());
        config.setMachineId(machineId);
        config.setMachineHost(host);
        config.setSessionId(id);
        config.setCols(connectInfo.getCols());
        config.setRows(connectInfo.getRows());
        config.setWidth(connectInfo.getWidth());
        config.setHeight(connectInfo.getHeight());
        config.setTerminalType(terminalType);
        TerminalOperateHandler terminalHandler = new TerminalOperateHandler(token, config, session, sessionStore);
        try {
            // 打开shell
            this.openShell(terminalHandler);
        } catch (Exception e) {
            session.close(WsCloseCode.OPEN_SHELL_EXCEPTION.close());
            log.error("terminal 建立连接失败-打开shell失败 host: {}, uid: {}, {}", host, tokenUserId, e);
            return null;
        }
        terminalSessionManager.getSessionStore().put(token, terminalHandler);
        log.info("terminal 建立连接成功, uid: {}, machineId: {}", tokenUserId, machineId);
        return terminalHandler;
    }

    /**
     * 打开shell
     */
    private void openShell(TerminalOperateHandler terminalHandler) {
        RuntimeException ex = null;
        for (int i = 0, t = MachineConst.CONNECT_RETRY_TIMES; i < t; i++) {
            log.info("terminal 建立连接-尝试打开shell 第{}次尝试 token: {}", (i + 1), terminalHandler.getToken());
            try {
                terminalHandler.connect();
                return;
            } catch (RuntimeException e) {
                // retry
                ex = e;
            }
        }
        throw ex;
    }

}
