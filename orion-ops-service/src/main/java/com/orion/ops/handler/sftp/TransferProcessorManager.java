package com.orion.ops.handler.sftp;

import com.alibaba.fastjson.JSON;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.utils.collect.Lists;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 14:56
 */
@Component
public class TransferProcessorManager {

    /**
     * key: token
     * value: processor
     */
    private Map<String, FileTransferProcessor> transferProcessor = new ConcurrentHashMap<>();

    /**
     * key: sessionId
     * value: webSocketSession
     */
    private Map<String, WebSocketSession> idMapping = new ConcurrentHashMap<>();

    /**
     * key: sessionId
     * value: userId_machineId
     */
    private Map<String, String> sessionUserMachineMapping = new ConcurrentHashMap<>();

    /**
     * key: userId_machineId
     * value: sessionIdList
     */
    private Map<String, List<String>> userMachineSessionMapping = new ConcurrentHashMap<>();

    /**
     * 添加processor
     *
     * @param token     token
     * @param processor processor
     */
    public void addProcessor(String token, FileTransferProcessor processor) {
        transferProcessor.put(token, processor);
    }

    /**
     * 删除processor
     *
     * @param token token
     */
    public void removeProcessor(String token) {
        transferProcessor.remove(token);
    }

    /**
     * 获取processor
     *
     * @param token token
     * @return processor
     */
    public FileTransferProcessor getProcessor(String token) {
        return transferProcessor.get(token);
    }

    /**
     * 认证session 通知
     *
     * @param id        id
     * @param session   session
     * @param userId    userId
     * @param machineId machineId
     */
    public void authSessionNotify(String id, WebSocketSession session, Long userId, Long machineId) {
        String userMachine = userId + "_" + machineId;
        idMapping.put(id, session);
        sessionUserMachineMapping.put(id, userMachine);
        userMachineSessionMapping.computeIfAbsent(userMachine, s -> new ArrayList<>()).add(id);
    }

    /**
     * 关闭session 通知
     *
     * @param id id
     */
    public void closeSessionNotify(String id) {
        idMapping.remove(id);
        String userMachine = sessionUserMachineMapping.remove(id);
        if (userMachine == null) {
            return;
        }
        List<String> sessionIds = userMachineSessionMapping.get(userMachine);
        if (sessionIds == null) {
            return;
        }
        sessionIds.removeIf(s -> s.equals(id));
    }

    @SneakyThrows
    public void notifySession(String userMachine, FileTransferNotifyDTO notify) {
        List<String> sessionIds = userMachineSessionMapping.get(userMachine);
        if (Lists.isEmpty(sessionIds)) {
            return;
        }
        for (String sessionId : sessionIds) {
            WebSocketSession session = idMapping.get(sessionId);
            if (!session.isOpen()) {
                continue;
            }
            // 通知
            session.sendMessage(new TextMessage(JSON.toJSONString(notify)));
        }
    }

}
