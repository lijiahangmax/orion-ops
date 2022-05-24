package com.orion.ops.handler.terminal;

import com.orion.lang.wrapper.Tuple;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.terminal.TerminalOperate;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.consts.ws.WsProtocol;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalConnectDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.WebSockets;
import com.orion.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
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

    private static final String CONNECTED_KEY = "connected";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = session.getId();
        String token = getToken(session);
        // token绑定
        String bindKey = Strings.format(KeyConst.TERMINAL_BIND_TOKEN, token);
        redisTemplate.opsForValue().set(bindKey, id, KeyConst.TERMINAL_BIND_EXPIRE, TimeUnit.SECONDS);
        // 刷新token 过期时间
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        redisTemplate.expire(tokenKey, KeyConst.TERMINAL_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        session.sendMessage(new TextMessage(WsProtocol.ACK.get()));
        log.info("terminal 建立ws连接 token: {}, id: {}", token, id);
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
    private static Tuple parsePayload(String payload) {
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

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (!(message instanceof TextMessage)) {
            return;
        }
        String token = getToken(session);
        String id = session.getId();
        String payload = ((TextMessage) message).getPayload();
        try {
            // 解析请求
            Tuple tuple = parsePayload(payload);
            if (tuple == null) {
                session.sendMessage(new TextMessage(WsProtocol.ILLEGAL_BODY.get()));
                return;
            }
            TerminalOperate operate = tuple.get(0);
            String body = tuple.get(1);
            // 执行
            if (operate == TerminalOperate.CONNECT) {
                // 建立连接
                if (session.getAttributes().get(CONNECTED_KEY) != null) {
                    return;
                }
                this.connect(session, id, token, body);
                return;
            }
            // 获取连接
            if (session.getAttributes().get(CONNECTED_KEY) == null) {
                session.close(WsCloseCode.VALID.close());
                return;
            }
            IOperateHandler handler = terminalSessionManager.getSession(token);
            if (handler == null) {
                session.close(WsCloseCode.UNKNOWN_CONNECT.close());
                return;
            }
            // 操作
            handler.handleMessage(operate, body);
        } catch (Exception e) {
            log.error("terminal 处理操作异常 token: {}, payload: {}, e: {}", token, payload, e);
            e.printStackTrace();
            if (session.isOpen()) {
                session.close(WsCloseCode.RUNTIME_EXCEPTION.close());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("terminal 操作异常拦截 token: {}, e: {}", getToken(session), exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String id = session.getId();
        int code = status.getCode();
        String token = getToken(session);
        String accessKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String bindKey = Strings.format(KeyConst.TERMINAL_BIND_TOKEN, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (!id.equals(bindValue)) {
            return;
        }
        log.info("terminal 关闭连接 token: {}, id:{}, code: {}, reason: {}", token, id, code, status.getReason());
        redisTemplate.delete(accessKey);
        redisTemplate.delete(bindKey);
        // 释放资源
        IOperateHandler handler = terminalSessionManager.removeSession(token);
        if (handler == null) {
            return;
        }
        handler.disconnect();
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
     * @param id      id
     * @param token   token
     * @param body    body
     */
    private void connect(WebSocketSession session, String id, String token, String body) throws IOException {
        log.info("terminal 尝试建立连接 token: {}, id: {}, body: {}", token, id, body);
        // 检查参数
        TerminalConnectDTO connectInfo = TerminalConnectDTO.parse(body);
        if (connectInfo == null) {
            session.sendMessage(new TextMessage(WsProtocol.MISS_ARGUMENT.get()));
            return;
        }
        // 获取token信息
        Long tokenUserId = MachineTerminalService.getTokenUserId(token);
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        Long machineId = Optional.ofNullable(redisTemplate.opsForValue().get(tokenKey))
                .map(Long::valueOf)
                .orElse(null);
        if (machineId == null) {
            log.info("terminal 建立连接拒绝-token认证失败 token: {}", token);
            session.close(WsCloseCode.INCORRECT_TOKEN.close());
            return;
        }
        // 检查绑定
        String bindKey = Strings.format(KeyConst.TERMINAL_BIND_TOKEN, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (bindValue == null || !bindValue.equals(id)) {
            log.info("terminal 建立连接拒绝-bind认证失败 token: {}", token);
            session.close(WsCloseCode.IDENTITY_MISMATCH.close());
            return;
        }
        // 检查操作用户
        UserDTO userDTO = passportService.getUserByToken(connectInfo.getLoginToken(), null);
        if (userDTO == null || !tokenUserId.equals(userDTO.getId())) {
            log.info("terminal 建立连接拒绝-用户认证失败 token: {}", token);
            session.close(WsCloseCode.IDENTITY_MISMATCH.close());
            return;
        }
        // 获取机器信息
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        if (machine == null) {
            log.info("terminal 建立连接拒绝-未查询到机器信息 token: {}, machineId: {}", token, machineId);
            session.close(WsCloseCode.INVALID_MACHINE.close());
            return;
        }
        // 删除token
        redisTemplate.delete(tokenKey);
        session.getAttributes().put(CONNECTED_KEY, 1);
        // 建立连接
        SessionStore sessionStore;
        try {
            // 打开session
            sessionStore = machineInfoService.openSessionStore(machine);
        } catch (Exception e) {
            WebSockets.openSessionStoreThrowClose(session, e);
            log.error("terminal 建立连接失败-连接远程服务器失败 uid: {}, machineId: {}, e: {}", tokenUserId, machineId, e);
            return;
        }
        // 配置
        TerminalConnectHint hint = new TerminalConnectHint();
        String terminalType = machineTerminalService.getMachineConfig(machineId).getTerminalType();
        hint.setUserId(tokenUserId);
        hint.setUsername(userDTO.getUsername());
        hint.setMachineId(machineId);
        hint.setMachineName(machine.getMachineName());
        hint.setMachineHost(machine.getMachineHost());
        hint.setMachineTag(machine.getMachineTag());
        hint.setCols(connectInfo.getCols());
        hint.setRows(connectInfo.getRows());
        hint.setWidth(connectInfo.getWidth());
        hint.setHeight(connectInfo.getHeight());
        hint.setTerminalType(terminalType);
        TerminalOperateHandler terminalHandler = new TerminalOperateHandler(token, hint, session, sessionStore);
        try {
            // 打开shell
            log.info("terminal 尝试建立连接-尝试打开shell token: {}", terminalHandler.getToken());
            terminalHandler.connect();
            log.info("terminal 建立连接成功-打开shell成功 token: {}", terminalHandler.getToken());
        } catch (Exception e) {
            session.close(WsCloseCode.OPEN_SHELL_EXCEPTION.close());
            log.error("terminal 建立连接失败-打开shell失败 machineId: {}, uid: {}, {}", machineId, tokenUserId, e);
            return;
        }
        terminalSessionManager.addSession(token, terminalHandler);
        session.sendMessage(new TextMessage(WsProtocol.CONNECTED.get()));
        log.info("terminal 建立连接成功 uid: {}, machineId: {}", tokenUserId, machineId);
    }

}
