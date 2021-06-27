package com.orion.ops.handler.sftp.download;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.handler.sftp.FileTransferHint;
import com.orion.ops.handler.sftp.FileTransferProcessor;
import com.orion.remote.channel.SessionStore;
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

    public DownloadFileProcessor(FileTransferHint hint, SessionStore sessionStore) {
        super(hint, sessionStore);
    }

    @Override
    public void exec() {
        log.info("sftp文件下载-提交任务 machineId: {}, remote: {}, hint: {}", hint.getMachineId(), hint.getRemoteFile(), JSON.toJSONString(hint));
        Threads.start(this, SchedulerPools.SFTP_SCHEDULER);
    }

    @Override
    public void resume() {
        super.resume = true;
        log.info("sftp文件下载-恢复传输 machineId: {}, remote: {}, hint: {}", hint.getMachineId(), hint.getRemoteFile(), JSON.toJSONString(hint));
        Threads.start(this, SchedulerPools.SFTP_SCHEDULER);
    }

    @Override
    protected void handler() {
        String remoteFile = hint.getRemoteFile();
        String localFile = MachineEnvAttr.SWAP_PATH.getValue() + hint.getLocalFile();
        log.info("sftp文件下载-开始传输 token: {}, machineId: {}, remote: {}, localFile: {}", token, hint.getMachineId(), remoteFile, localFile);
        SftpDownload download = executor.download(remoteFile, localFile);
        this.initProgress(download.getProgress());
        download.run();
    }
}
