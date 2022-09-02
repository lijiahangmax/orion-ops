package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.io.Files1;
import com.orion.net.remote.channel.sftp.SftpDownloader;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.handler.sftp.FileTransferProcessor;
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

    public DownloadFileProcessor(FileTransferLogDO record) {
        super(record);
    }

    @Override
    public void exec() {
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件下载-提交任务 fileToken: {}, machineId: {}, local: {}, remote: {}, record: {}",
                fileToken, machineId, localAbsolutePath, record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(SystemEnvAttr.SWAP_PATH.getValue(), localFile);
        log.info("sftp文件下载-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, machineId, localAbsolutePath, remoteFile);
        SftpDownloader download = executor.download(remoteFile, localAbsolutePath);
        this.initProgress(download.getProgress());
        download.run();
    }

}
