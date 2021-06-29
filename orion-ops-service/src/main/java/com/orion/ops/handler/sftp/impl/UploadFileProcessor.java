package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.handler.sftp.FileTransferHint;
import com.orion.ops.handler.sftp.FileTransferProcessor;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.bigfile.SftpUpload;
import com.orion.utils.Threads;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/29 16:32
 */
@Slf4j
public class UploadFileProcessor extends FileTransferProcessor {

    public UploadFileProcessor(FileTransferHint hint, SessionStore sessionStore) {
        super(hint, sessionStore);
    }

    @Override
    public void exec() {
        log.info("sftp文件上传-提交任务 machineId: {}, local: {}, remote: {}, hint: {}", hint.getMachineId(), hint.getLocalFile(), hint.getRemoteFile(), JSON.toJSONString(hint));
        Threads.start(this, SchedulerPools.SFTP_SCHEDULER);
    }

    @Override
    public void resume() {
        super.resume = true;
        log.info("sftp文件上传-恢复传输 machineId: {}, local: {}, remote: {}, hint: {}", hint.getMachineId(), hint.getLocalFile(), hint.getRemoteFile(), JSON.toJSONString(hint));
        Threads.start(this, SchedulerPools.SFTP_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = hint.getRemoteFile();
        String localFile = MachineEnvAttr.SWAP_PATH.getValue() + hint.getLocalFile();
        log.info("sftp文件上传-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}", fileToken, hint.getMachineId(), localFile, remoteFile);
        SftpUpload upload = executor.upload(remoteFile, localFile);
        this.initProgress(upload.getProgress());
        upload.run();
    }

}
