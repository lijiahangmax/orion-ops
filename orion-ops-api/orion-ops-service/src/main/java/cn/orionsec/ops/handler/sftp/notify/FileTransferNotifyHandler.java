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
package cn.orionsec.ops.handler.sftp.notify;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.entity.dto.sftp.SftpSessionTokenDTO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.handler.sftp.TransferProcessorManager;
import cn.orionsec.ops.service.api.PassportService;
import cn.orionsec.ops.service.api.SftpService;
import cn.orionsec.ops.utils.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String id = session.getId();
        String token = WebSockets.getToken(session);
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
            session.getAttributes().put(WebSockets.UID, userId);
            session.getAttributes().put(WebSockets.MID, machineIdList);
            log.info("sftp-Notify 建立连接成功 id: {}, token: {}, userId: {}, machineId: {}, machineIdList: {}", id, token, userId, machineId, machineIdList);
        } catch (Exception e) {
            log.error("sftp-Notify 建立连接失败-未查询到token信息 id: {}, token: {}", id, token, e);
            WebSockets.close(session, WsCloseCode.FORGE_TOKEN);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        String id = session.getId();
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.get(WebSockets.AUTHED) != null) {
            return;
        }
        if (!(message instanceof TextMessage)) {
            return;
        }
        // 获取body
        String authToken = ((TextMessage) message).getPayload();
        if (Strings.isEmpty(authToken)) {
            log.info("sftp-Notify 认证失败-body为空 id: {}", id);
            WebSockets.close(session, WsCloseCode.INCORRECT_TOKEN);
            return;
        }
        // 获取认证用户
        UserDTO user = passportService.getUserByToken(authToken, null);
        if (user == null) {
            log.info("sftp-Notify 认证失败-未查询到用户 id: {}, authToken: {}", id, authToken);
            WebSockets.close(session, WsCloseCode.INCORRECT_TOKEN);
            return;
        }
        // 检查认证用户是否匹配
        Long userId = user.getId();
        Long tokenUserId = (Long) attributes.get(WebSockets.UID);
        final boolean valid = userId.equals(tokenUserId);
        if (!valid) {
            log.info("sftp-Notify 认证失败-用户不匹配 id: {}, userId: {}, tokenUserId: {}", id, userId, tokenUserId);
            WebSockets.close(session, WsCloseCode.VALID);
            return;
        }
        attributes.put(WebSockets.AUTHED, Const.ENABLE);
        // 注册会话
        List<Long> machineIdList = (List<Long>) attributes.get(WebSockets.MID);
        Lists.forEach(machineIdList, i -> {
            log.info("sftp-Notify 认证成功 id: {}, userId: {}, machineId: {}", id, userId, i);
            transferProcessorManager.registerSessionNotify(id, session, userId, i);
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("sftp-Notify 操作异常拦截 id: {}", session.getId());
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
