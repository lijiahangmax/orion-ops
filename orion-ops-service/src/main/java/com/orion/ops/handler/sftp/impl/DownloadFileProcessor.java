package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.handler.sftp.FileTransferProcessor;
import com.orion.remote.channel.sftp.bigfile.SftpDownload;
import com.orion.utils.Threads;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件下载处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/25 18:38
 */
@Slf4j
public class DownloadFileProcessor extends FileTransferProcessor {

    public DownloadFileProcessor(FileTransferLogDO record, String charset) {
        super(record, charset);
    }

    @Override
    public void exec() {
        log.info("sftp文件下载-提交任务 machineId: {}, local: {}, remote: {}, record: {}",
                record.getMachineId(), record.getLocalFile(), record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER);
    }

    @Override
    public void resume() {
        log.info("sftp文件下载-恢复传输 machineId: {}, local: {}, remote: {}, record: {}",
                record.getMachineId(), record.getLocalFile(), record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        log.info("sftp文件下载-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, record.getMachineId(), localFile, remoteFile);
        SftpDownload download = executor.download(remoteFile, localFile);
        this.initProgress(download.getProgress());
        download.run();
    }

}
