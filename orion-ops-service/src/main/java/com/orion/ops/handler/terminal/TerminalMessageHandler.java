package com.orion.ops.handler.terminal;

import com.alibaba.fastjson.JSON;
import com.orion.exception.AuthenticationException;
import com.orion.exception.ConnectionRuntimeException;
import com.orion.id.UUIds;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.protocol.TerminalCloseCode;
import com.orion.ops.consts.protocol.TerminalConst;
import com.orion.ops.consts.protocol.TerminalOperate;
import com.orion.ops.consts.protocol.TerminalProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalConnectDTO;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;
import com.orion.ops.entity.dto.UserDTO;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        String token = this.getToken(session);
        // 检查伪造token
        Long tokenUserId = MachineTerminalService.getTokenUserId(token);
        if (tokenUserId == null) {
            session.close(TerminalCloseCode.FORGE_TOKEN.close());
            return;
        }
        // 检查token
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String v = redisTemplate.opsForValue().get(tokenKey);
        // 未查询到token
        if (v == null) {
            session.close(TerminalCloseCode.INCORRECT_TOKEN.close());
            return;
        }
        // 刷新token 过期时间
        redisTemplate.expire(tokenKey, TerminalConst.TERMINAL_TOKEN_EXPIRE_S, TimeUnit.SECONDS);
        // 握手设置id
        String id = UUIds.random19();
        String idKey = Strings.format(KeyConst.TERMINAL_ID, id);
        redisTemplate.opsForValue().set(idKey, token, TerminalConst.TERMINAL_ID_EXPIRE_S, TimeUnit.SECONDS);
        session.sendMessage(new TextMessage(TerminalProtocol.ACK.msg(id)));
        log.info("terminal 建立ws连接 token: {}", token);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String token = this.getToken(session);
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
                session.sendMessage(new TextMessage(TerminalProtocol.ILLEGAL_BODY.get()));
                return;
            }
            // 操作
            TerminalOperate operate = TerminalOperate.of(data.getOperate());
            if (operate == null) {
                session.sendMessage(new TextMessage(TerminalProtocol.UNKNOWN_OPERATE.get()));
                return;
            }
            // 检查参数
            String id = data.getId();
            if (id == null) {
                session.sendMessage(new TextMessage(TerminalProtocol.ARGUMENT.get()));
                return;
            }

            TerminalHandler handler;
            if (operate == TerminalOperate.CONNECT) {
                // 建立连接
                handler = this.connect(session, data, token);
                if (handler == null) {
                    return;
                }
            } else {
                // 获取
                handler = terminalSessionManager.sessionStore.get(token);
            }
            // 未找到连接
            if (handler == null) {
                session.close(TerminalCloseCode.UNKNOWN_CONNECT.close());
                return;
            }
            // 认证
            valid = handler.valid(id);
            if (!valid) {
                session.close(TerminalCloseCode.VALID.close());
                return;
            }
            // 操作
            handler.handleMessage(data, operate);
        } catch (Exception e) {
            log.error("terminal 处理操作异常 token: {}, data: {}, e: {}", token, data, e);
            e.printStackTrace();
            if (valid) {
                session.close(TerminalCloseCode.RUNTIME_VALID_EXCEPTION.close());
            } else {
                session.close(TerminalCloseCode.RUNTIME_EXCEPTION.close());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String token = this.getToken(session);
        log.error("terminal 操作异常拦截 token: {}, e: {}", token, exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = this.getToken(session);
        int code = status.getCode();
        log.info("terminal 关闭连接 token: {} code: {} reason: {}", token, code, status.getReason());
        // 释放资源
        TerminalCloseCode type = TerminalCloseCode.of(code);
        if (TerminalCloseCode.HEART_DOWN.equals(type) ||
                TerminalCloseCode.RUNTIME_VALID_EXCEPTION.equals(type) ||
                TerminalCloseCode.FORCED_OFFLINE.equals(type)) {
            TerminalHandler handler = terminalSessionManager.sessionStore.get(token);
            if (handler == null) {
                return;
            }
            handler.disconnect();
        }
        boolean released = type == null ||
                TerminalCloseCode.HEART_DOWN.equals(type) ||
                TerminalCloseCode.FORCED_OFFLINE.equals(type) ||
                TerminalCloseCode.DISCONNECT.equals(type) ||
                TerminalCloseCode.EOF_CALLBACK.equals(type);
        if (!released) {
            return;
        }
        terminalSessionManager.sessionStore.remove(token);
        redisTemplate.delete(Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token));
        // log
        MachineTerminalLogDO updateLog = new MachineTerminalLogDO();
        updateLog.setCloseCode(code);
        updateLog.setDisconnectedTime(new Date());
        Integer logUpdateEffect = machineTerminalService.updateAccessLog(token, updateLog);
        log.info("terminal 连接关闭更新日志 token: {} effect: {}", token, logUpdateEffect);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取urlToken
     *
     * @param session session
     * @return token
     */
    private String getToken(WebSocketSession session) {
        String[] uri = Objects.requireNonNull(session.getUri()).toString().split("/");
        return uri[uri.length - 1];
    }

    /**
     * 建立连接
     *
     * @param session session
     * @param data    data
     * @return handler
     */
    private TerminalOperateHandler connect(WebSocketSession session, TerminalDataTransferDTO data, String token) throws IOException {
        log.info("terminal 尝试建立连接 token: {} data: {}", token, JSON.toJSONString(data));
        // 检查参数
        TerminalConnectDTO connectInfo = null;
        try {
            connectInfo = JSON.parseObject(data.getBody(), TerminalConnectDTO.class);
        } catch (Exception e) {
            // ignore
        }
        if (connectInfo == null) {
            session.sendMessage(new TextMessage(TerminalProtocol.ARGUMENT.get()));
            return null;
        }
        String loginToken = connectInfo.getLoginToken();
        if (Strings.isBlank(loginToken) ||
                connectInfo.getRows() == null || connectInfo.getCols() == null ||
                connectInfo.getWidth() == null || connectInfo.getHeight() == null) {
            session.sendMessage(new TextMessage(TerminalProtocol.ARGUMENT.get()));
            return null;
        }
        // 检查伪造token
        Long tokenUserId = MachineTerminalService.getTokenUserId(token);
        if (tokenUserId == null) {
            log.info("terminal 建立连接驳回-伪造token token: {}", token);
            session.close(TerminalCloseCode.FORGE_TOKEN.close());
            return null;
        }
        // 检查token
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        if (tokenValue == null) {
            log.info("terminal 建立连接驳回-token不存在 token: {}", token);
            session.close(TerminalCloseCode.INCORRECT_TOKEN.close());
            return null;
        }
        // 检查id
        String idKey = Strings.format(KeyConst.TERMINAL_ID, data.getId());
        String idValue = redisTemplate.opsForValue().get(idKey);
        if (idValue == null || !idValue.equals(token)) {
            log.info("terminal 建立连接驳回-tokenId认证失败 token: {}", token);
            session.close(TerminalCloseCode.IDENTITY_MISMATCH.close());
            return null;
        }
        // 检查操作用户
        UserDTO userDTO = passportService.getUserByToken(loginToken);
        if (userDTO == null || !tokenUserId.equals(userDTO.getId())) {
            log.info("terminal 建立连接驳回-用户认证失败 token: {}", token);
            session.close(TerminalCloseCode.IDENTITY_MISMATCH.close());
            return null;
        }
        // 刷新token 过期时间
        redisTemplate.expire(tokenKey, TerminalConst.TERMINAL_TOKEN_EXPIRE_S, TimeUnit.SECONDS);
        // 建立连接
        Long machineId = Long.valueOf(tokenValue);
        SessionStore sessionStore;
        try {
            // 打开session
            sessionStore = this.openSession(machineId);
        } catch (Exception e) {
            if (e instanceof ConnectionRuntimeException) {
                session.close(TerminalCloseCode.CONNECTED_FAILURE.close());
            } else if (e instanceof AuthenticationException) {
                session.close(TerminalCloseCode.CONNECTED_AUTH_FAILURE.close());
            } else {
                session.close(TerminalCloseCode.CONNECTED_EXCEPTION.close());
            }
            log.error("terminal 建立连接失败-连接远程服务器失败 uid: {}, {}", tokenUserId, e);
            return null;
        }
        // 配置
        String host = sessionStore.getHost();
        TerminalConnectConfig config = new TerminalConnectConfig();
        String terminalType = machineTerminalService.getMachineConfig(machineId).getTerminalType();
        config.setUserId(tokenUserId);
        config.setUsername(userDTO.getUsername());
        config.setMachineId(machineId);
        config.setMachineHost(host);
        config.setSessionId(data.getId());
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
            session.close(TerminalCloseCode.OPEN_SHELL_EXCEPTION.close());
            log.error("terminal 建立连接失败-打开shell失败 host: {}, uid: {}, {}", host, tokenUserId, e);
            return null;
        }
        terminalSessionManager.sessionStore.put(token, terminalHandler);
        log.info("terminal 建立连接成功 uid: {} machineId: {}", tokenUserId, machineId);
        return terminalHandler;
    }

    /**
     * 打开session
     */
    private SessionStore openSession(Long machineId) {
        ConnectionRuntimeException ex = null;
        for (int i = 0, t = TerminalConst.TERMINAL_CONNECT_RETRY_TIMES; i < t; i++) {
            log.info("terminal 建立连接-尝试连接远程服务器 第{}次尝试 machineId: {}", (i + 1), machineId);
            try {
                return machineInfoService.openSessionStore(machineId);
            } catch (Exception e) {
                if (e instanceof ConnectionRuntimeException) {
                    // retry
                    ex = (ConnectionRuntimeException) e;
                } else if (e instanceof AuthenticationException) {
                    throw e;
                } else {
                    throw e;
                }
            }
        }
        throw ex;
    }

    /**
     * 打开shell
     */
    private void openShell(TerminalOperateHandler terminalHandler) {
        RuntimeException ex = null;
        for (int i = 0, t = TerminalConst.TERMINAL_CONNECT_RETRY_TIMES; i < t; i++) {
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
