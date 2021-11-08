package com.orion.ops.handler.sftp;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.sftp.SftpNotifyType;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.utils.Exceptions;
import com.orion.utils.Threads;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.json.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 14:56
 */
@Slf4j
@Component
public class TransferProcessorManager {

    /**
     * key: token
     * value: processor
     */
    private final Map<String, IFileTransferProcessor> transferProcessor = Maps.newCurrentHashMap();

    /**
     * key: sessionId
     * value: webSocketSession
     */
    private final Map<String, WebSocketSession> idMapping = Maps.newCurrentHashMap();

    /**
     * key: sessionId
     * value: userId_machineId
     */
    private final Map<String, String> sessionUserMachineMapping = Maps.newCurrentHashMap();

    /**
     * key: userId_machineId
     * value: sessionIdList
     */
    private final Map<String, List<String>> userMachineSessionMapping = Maps.newCurrentHashMap();

    /**
     * 添加processor
     *
     * @param token     token
     * @param processor processor
     */
    public void addProcessor(String token, IFileTransferProcessor processor) {
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
    public IFileTransferProcessor getProcessor(String token) {
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
        String userMachine = this.getUserMachine(userId, machineId);
        idMapping.put(id, session);
        sessionUserMachineMapping.put(id, userMachine);
        userMachineSessionMapping.computeIfAbsent(userMachine, s -> Lists.newList()).add(id);
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

    /**
     * 通知session 添加事件
     *
     * @param userId    userId
     * @param machineId machineId
     * @param fileToken fileToken
     * @param record    record
     */
    public void notifySessionAddEvent(Long userId, Long machineId, String fileToken, FileTransferLogDO record) {
        FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
        notify.setType(SftpNotifyType.ADD.getType());
        notify.setFileToken(fileToken);
        notify.setBody(Jsons.toJsonWriteNull(Converts.to(record, FileTransferLogVO.class)));
        this.notifySession(userId, machineId, notify);
    }

    /**
     * 通知session 进度事件
     *
     * @param userId    userId
     * @param machineId machineId
     * @param fileToken fileToken
     * @param progress  progress
     */
    public void notifySessionProgressEvent(Long userId, Long machineId, String fileToken, FileTransferNotifyDTO.FileTransferNotifyProgress progress) {
        FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
        notify.setType(SftpNotifyType.PROGRESS.getType());
        notify.setFileToken(fileToken);
        notify.setBody(Jsons.toJsonWriteNull(progress));
        this.notifySession(userId, machineId, notify);
    }

    /**
     * 通知session 状态事件
     *
     * @param userId    userId
     * @param machineId machineId
     * @param fileToken fileToken
     * @param status    status
     */
    public void notifySessionStatusEvent(Long userId, Long machineId, String fileToken, Integer status) {
        FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
        notify.setType(SftpNotifyType.CHANGE_STATUS.getType());
        notify.setFileToken(fileToken);
        notify.setBody(status);
        this.notifySession(userId, machineId, notify);
    }

    /**
     * 通知session
     *
     * @param userId    userId
     * @param machineId machineId
     * @param notify    notifyInfo
     */
    public void notifySession(Long userId, Long machineId, FileTransferNotifyDTO notify) {
        List<String> sessionIds = userMachineSessionMapping.get(this.getUserMachine(userId, machineId));
        if (Lists.isEmpty(sessionIds)) {
            return;
        }
        for (String sessionId : sessionIds) {
            WebSocketSession session = idMapping.get(sessionId);
            if (session == null || !session.isOpen()) {
                continue;
            }
            // 通知
            Exception ex = null;
            for (int i = 0; i < 3; i++) {
                try {
                    session.sendMessage(new TextMessage(Jsons.toJsonWriteNull(notify)));
                    break;
                } catch (Exception e) {
                    ex = e;
                    log.error("通知session失败 userId: {}, machineId: {} sessionId: {}, try: {}, e: {}", userId, machineId, session, (i + 1), Exceptions.getDigest(e));
                    Threads.sleep(Const.N_100);
                }
            }
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取用户机器key
     *
     * @param userId    userId
     * @param machineId machineId
     * @return key
     */
    private String getUserMachine(Long userId, Long machineId) {
        return userId + "_" + machineId;
    }

}
