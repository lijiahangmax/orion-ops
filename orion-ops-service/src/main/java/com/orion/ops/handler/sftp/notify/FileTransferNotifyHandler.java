package com.orion.ops.handler.sftp.notify;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.ws.WsCloseCode;
import com.orion.ops.entity.dto.SftpSessionTokenDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.handler.sftp.TransferProcessorManager;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.service.api.SftpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.orion.ops.utils.WebSockets.getToken;

/**
 * sftp 传输通知处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 11:25
 */
@Component
@Slf4j
public class FileTransferNotifyHandler implements WebSocketHandler {

    @Resource
    private TransferProcessorManager transferProcessorManager;

    @Resource
    private PassportService passportService;

    @Resource
    private SftpService sftpService;

    private static final String USER_ID_KEY = "userId";

    private static final String MACHINE_ID_LIST_KEY = "machineIdList";

    private static final String AUTH_KEY = "auth";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = session.getId();
        String token = getToken(session);
        try {
            SftpSessionTokenDTO tokenInfo = sftpService.getTokenInfo(token);
            Long userId = tokenInfo.getUserId();
            Long machineId = tokenInfo.getMachineId();
            List<Long> machineIdList = tokenInfo.getMachineIdList();
            if (machineIdList == null) {
                machineIdList = Lists.newList();
            }
            if (machineId != null) {
                machineIdList.add(machineId);
            }
            session.getAttributes().put(USER_ID_KEY, userId);
            session.getAttributes().put(MACHINE_ID_LIST_KEY, machineIdList);
            log.info("sftp-Notify 建立连接成功 id: {}, token: {}, userId: {}, machineId: {}, machineIdList: {}", id, token, userId, machineId, machineIdList);
        } catch (Exception e) {
            log.error("sftp-Notify 建立连接失败-未查询到token信息 id: {}, token: {}", id, token, e);
            session.close(WsCloseCode.FORGE_TOKEN.status());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String id = session.getId();
        Map<String, Object> attributes = session.getAttributes();
        Object auth = attributes.get(AUTH_KEY);
        if (auth != null) {
            return;
        }
        if (!(message instanceof TextMessage)) {
            return;
        }
        // 获取body
        String authToken = ((TextMessage) message).getPayload();
        if (Strings.isEmpty(authToken)) {
            log.info("sftp-Notify 认证失败-body为空 id: {}", id);
            session.close(WsCloseCode.INCORRECT_TOKEN.status());
            return;
        }
        // 获取认证用户
        UserDTO user = passportService.getUserByToken(authToken, null);
        if (user == null) {
            log.info("sftp-Notify 认证失败-未查询到用户 id: {}, authToken: {}", id, authToken);
            session.close(WsCloseCode.INCORRECT_TOKEN.status());
            return;
        }
        // 检查认证用户是否匹配
        Long userId = user.getId();
        Long tokenUserId = (Long) attributes.get(USER_ID_KEY);
        final boolean valid = userId.equals(tokenUserId);
        if (!valid) {
            log.info("sftp-Notify 认证失败-用户不匹配 id: {}, userId: {}, tokenUserId: {}", id, userId, tokenUserId);
            session.close(WsCloseCode.VALID.status());
            return;
        }
        attributes.put(AUTH_KEY, Const.ENABLE);
        // 注册会话
        List<Long> machineIdList = (List<Long>) attributes.get(MACHINE_ID_LIST_KEY);
        Lists.forEach(machineIdList, i -> {
            log.info("sftp-Notify 认证成功 id: {}, userId: {}, machineId: {}", id, userId, i);
            transferProcessorManager.registerSessionNotify(id, session, userId, i);
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("sftp-Notify 操作异常拦截 id: {}, e: {}", session.getId(), exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String id = session.getId();
        transferProcessorManager.closeSessionNotify(id);
        log.info("sftp-Notify 关闭连接 id: {}, code: {}, reason: {}", id, status.getCode(), status.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
