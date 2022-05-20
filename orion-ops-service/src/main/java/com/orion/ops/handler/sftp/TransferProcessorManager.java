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
 * 文件传输管理器
 *
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
     * sessionId 和 session 映射
     * <p>
     * key: sessionId
     * value: webSocketSession
     */
    private final Map<String, WebSocketSession> idMapping = Maps.newCurrentHashMap();

    /**
     * 机器 和 session 映射
     * <p>
     * key: userId_machineId
     * value: sessionIdList
     */
    private final Map<String, List<String>> userMachineSessionMapping = Maps.newCurrentHashMap();

    /**
     * 文件传输进度
     * <p>
     * key: token
     * value: progress
     */
    private final Map<String, String> TRANSFER_PROGRESS = Maps.newMap();

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
     * 注册session 通知
     *
     * @param id        id
     * @param session   session
     * @param userId    userId
     * @param machineId machineId
     */
    public void registerSessionNotify(String id, WebSocketSession session, Long userId, Long machineId) {
        idMapping.put(id, session);
        userMachineSessionMapping.computeIfAbsent(this.getUserMachine(userId, machineId), s -> Lists.newList()).add(id);
    }

    /**
     * 关闭session 通知
     *
     * @param id id
     */
    public void closeSessionNotify(String id) {
        idMapping.remove(id);
        // 删除机器与会话的关联
        userMachineSessionMapping.forEach((k, v) -> {
            if (Lists.isEmpty(v)) {
                return;
            }
            v.removeIf(s -> s.equals(id));
        });
    }

    /**
     * 通知session 添加事件
     *
     * @param userId    userId
     * @param machineId machineId
     * @param record    record
     */
    public void notifySessionAddEvent(Long userId, Long machineId, FileTransferLogDO record) {
        FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
        notify.setType(SftpNotifyType.ADD.getType());
        notify.setFileToken(record.getFileToken());
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
        // 设置进度
        TRANSFER_PROGRESS.put(fileToken, progress.getProgress());
        // 通知
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
        // 清除进度
        TRANSFER_PROGRESS.remove(fileToken);
        // 通知
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
            if (sessionId == null) {
                continue;
            }
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

    /**
     * 获取传输进度
     *
     * @param token token
     * @return progress
     */
    public Double getProgress(String token) {
        String progress = TRANSFER_PROGRESS.get(token);
        if (progress == null) {
            return null;
        }
        return Double.valueOf(progress);
    }

}
