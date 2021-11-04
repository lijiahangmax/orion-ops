package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.handler.sftp.FileTransferProcessor;
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

    public UploadFileProcessor(FileTransferLogDO record, String charset) {
        super(record, charset);
    }

    @Override
    public void exec() {
        log.info("sftp文件上传-提交任务 machineId: {}, local: {}, remote: {}, record: {}",
                record.getMachineId(), record.getLocalFile(), record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_UPLOAD_SCHEDULER);
    }

    @Override
    public void resume() {
        log.info("sftp文件上传-恢复传输 machineId: {}, local: {}, remote: {}, record: {}",
                record.getMachineId(), record.getLocalFile(), record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_UPLOAD_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        log.info("sftp文件上传-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, record.getMachineId(), localFile, remoteFile);
        SftpUpload upload = executor.upload(remoteFile, localFile);
        this.initProgress(upload.getProgress());
        upload.run();
    }

}
