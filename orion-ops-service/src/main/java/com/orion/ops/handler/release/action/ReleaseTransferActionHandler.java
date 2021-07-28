package com.orion.ops.handler.release.action;

import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;

import java.io.File;

/**
 * 宿主机传输产物处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#TRANSFER
 * @since 2021/7/15 17:24
 */
public class ReleaseTransferActionHandler extends AbstractReleaseActionHandler {

    public ReleaseTransferActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        super(hint, action);
    }

    @Override
    protected void handleAction() throws Exception {
        String distPath = hint.getDistPath();
        this.appendLog("开始执行传输产物操作: {}", distPath);
        File distFile = new File(distPath);
        if (!Files1.isFile(distFile)) {
            throw Exceptions.log("产物文件不存在 " + distPath);
        }
        this.appendLog("size: {}, md5: {}", Files1.getSize(distFile), Files1.md5(distFile));

        // 备份产物 用于回滚
        Files1.copy(distFile, new File(hint.getDistSnapshotPath()));
        // 分发产物
        for (ReleaseMachineHint machine : hint.getMachines()) {
            Long machineId = machine.getMachineId();
            String targetPath = machine.getDistPath();
            String host = machine.getHost();
            this.appendLog("开始分发产物文件 host: {}, machineId: {}, path: {}", host, machineId, machineId);
            SftpExecutor executor = null;
            try {
                // 获取连接
                SessionStore session = hint.getSessionHolder().get(machineId);
                if (!session.isConnected()) {
                    session.connect();
                }
                // 获取执行器
                executor = session.getSftpExecutor();
                executor.connect();
                executor.uploadFile(targetPath, distFile);
                this.appendLog("分发产物文件成功 host: {}, machineId: {}, path: {}", host, machineId, targetPath);
            } catch (Exception e) {
                this.appendLog("分发产物文件失败 host: {}, machineId: {}, path: {}, err: {} {}", host, machineId, targetPath, e.getClass().getName(), e.getMessage());
                throw e;
            } finally {
                Streams.close(executor);
            }
        }
    }

    @Override
    protected void setLoggerAppender() {
        super.setLoggerAppender();
        appender.then(hint.getHostLogOutputStream()).onClose(false);
    }

}
