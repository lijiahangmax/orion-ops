package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.handler.sftp.FileTransferProcessor;
import com.orion.remote.channel.sftp.bigfile.SftpDownload;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

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
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + localFile);
        log.info("sftp文件下载-提交任务 fileToken: {}, machineId: {}, local: {}, remote: {}, record: {}",
                fileToken, machineId, localAbsolutePath, record.getRemoteFile(), JSON.toJSONString(record));
        Threads.start(this, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + localFile);
        log.info("sftp文件下载-开始传输 fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, machineId, localAbsolutePath, remoteFile);
        SftpDownload download = executor.download(remoteFile, localAbsolutePath);
        this.initProgress(download.getProgress());
        download.run();
    }

    @Override
    public void usingFsCopy() {
        String remoteFile = record.getRemoteFile();
        String localFile = record.getLocalFile();
        String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + localFile);
        log.info("sftp文件上传-使用FSC fileToken: {}, machineId: {}, local: {}, remote: {}",
                fileToken, machineId, localAbsolutePath, remoteFile);
        // 复制
        File sourceFile = new File(remoteFile);
        Files1.copy(sourceFile, new File(localAbsolutePath));
        // 通知进度
        long fileSize = sourceFile.length();
        super.notifyProgress(Files1.getSize(fileSize), Files1.getSize(fileSize), "100");
        // 通知状态
        super.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, fileSize);
    }

}
