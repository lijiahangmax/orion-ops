package com.orion.ops.handler.sftp;

import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.spring.SpringHolder;
import com.orion.support.progress.ByteTransferProgress;
import com.orion.support.progress.ByteTransferRateProgress;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Files1;
import com.orion.utils.math.Numbers;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * sftp 传输文件基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 14:58
 */
@Slf4j
public abstract class FileTransferProcessor implements IFileTransferProcessor {

    protected static MachineInfoService machineInfoService = SpringHolder.getBean("machineInfoService");

    protected static MachineEnvService machineEnvService = SpringHolder.getBean("machineEnvService");

    protected static FileTransferLogDAO fileTransferLogDAO = SpringHolder.getBean("fileTransferLogDAO");

    protected static TransferProcessorManager transferProcessorManager = SpringHolder.getBean("transferProcessorManager");

    protected SessionStore sessionStore;

    protected SftpExecutor executor;

    protected FileTransferLogDO record;

    protected Long userId;

    protected Long machineId;

    protected String fileToken;

    protected ByteTransferRateProgress progress;

    protected volatile boolean userCancel;

    public FileTransferProcessor(FileTransferLogDO record) {
        this.record = record;
        this.fileToken = record.getFileToken();
        this.userId = record.getUserId();
        this.machineId = record.getMachineId();
    }

    @Override
    public void run() {
        // 判断是否可以传输
        FileTransferLogDO fileTransferLog = fileTransferLogDAO.selectById(record.getId());
        if (Const.DISABLE.equals(fileTransferLog.getShowType())
                || !SftpTransferStatus.WAIT.getStatus().equals(fileTransferLog.getTransferStatus())) {
            return;
        }
        transferProcessorManager.addProcessor(fileToken, this);
        try {
            // 开始传输
            this.updateStatusAndNotify(SftpTransferStatus.RUNNABLE.getStatus());
            // 打开连接
            this.sessionStore = machineInfoService.openSessionStore(machineId);
            String charset = machineEnvService.getSftpCharset(machineId);
            this.executor = sessionStore.getSftpExecutor(charset);
            executor.connect();
            log.info("sftp传输文件-初始化完毕, 准备处理传输 fileToken: {}", fileToken);
            // 检查是否可以用文件系统传输
            if (this.checkUseFsCopy()) {
                // 直接拷贝
                this.usingFsCopy();
            } else {
                // 处理
                this.handler();
            }
            log.info("sftp传输文件-传输完毕 fileToken: {}", fileToken);
        } catch (Exception e) {
            log.error("sftp传输文件-出现异常 fileToken: {}, e: {}, message: {}", fileToken, e.getClass().getName(), e.getMessage());
            // 程序错误并非传输错误修改状态
            if (!userCancel) {
                log.error("sftp传输文件-运行异常 fileToken: {}", fileToken, e);
                this.updateStatusAndNotify(SftpTransferStatus.ERROR.getStatus());
            }
            e.printStackTrace();
        } finally {
            transferProcessorManager.removeProcessor(fileToken);
            this.disconnected();
        }
    }

    @Override
    public void stop() {
        log.info("sftp传输文件-用户暂停 fileToken: {}", fileToken);
        this.userCancel = true;
        this.updateStatusAndNotify(SftpTransferStatus.PAUSE.getStatus());
        this.disconnected();
    }

    /**
     * 处理操作
     */
    protected abstract void handler();

    /**
     * 检查是否可以使用fileSystem 拷贝文件
     *
     * @return 是否可用
     */
    protected boolean checkUseFsCopy() {
        // 创建一个临时文件
        String checkPath = Files1.getPath(MachineEnvAttr.TEMP_PATH.getValue() + "/" + UUIds.random32() + ".ck");
        File checkFile = new File(checkPath);
        Files1.touch(checkFile);
        checkFile.deleteOnExit();
        // 查询远程机器是否有此文件 如果有则证明传输机器和宿主机是同一台
        boolean exist = executor.getFile(checkFile.getAbsolutePath()) != null;
        Files1.delete(checkFile);
        return exist;
    }

    /**
     * 使用fileSystem 拷贝文件
     */
    protected abstract void usingFsCopy();

    /**
     * 初始化进度条
     */
    protected void initProgress(ByteTransferRateProgress progress) {
        this.progress = progress;
        progress.computeRate();
        progress.rateAcceptor(this::transferAccept);
        progress.callback(this::transferDoneCallback);
    }

    /**
     * 传输回调
     *
     * @param progress progress
     */
    protected void transferAccept(ByteTransferRateProgress progress) {
        try {
            if (progress.isDone()) {
                return;
            }
            String progressRate = Numbers.setScale(progress.getProgress() * 100, 2);
            String transferRate = Files1.getSize(progress.getNowRate());
            String transferCurrent = Files1.getSize(progress.getCurrent());
            // debug
            // log.info(transferCurrent + " " + progressRate + "% " + transferRate + "/s");
            // notify progress
            this.notifyProgress(transferRate, transferCurrent, progressRate);
        } catch (Exception e) {
            log.error("sftp-传输信息回调异常 fileToken: {}, digest: {}", fileToken, Exceptions.getDigest(e));
            e.printStackTrace();
        }
    }

    /**
     * 传输完成回调
     *
     * @param pro progress
     */
    protected void transferDoneCallback(ByteTransferProgress pro) {
        try {
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(record.getId());
            if (progress.isError()) {
                // 非用户取消更新状态
                if (!userCancel) {
                    this.updateStatusAndNotify(SftpTransferStatus.ERROR.getStatus());
                }
            } else {
                String transferCurrent = Files1.getSize(progress.getCurrent());
                String transferRate = Files1.getSize(progress.getNowRate());
                // notify progress
                this.notifyProgress(transferRate, transferCurrent, "100");
                // notify status
                this.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, progress.getEnd());
            }
        } catch (Exception e) {
            log.error("sftp-传输完成回调异常 fileToken: {}, digest: {}", fileToken, Exceptions.getDigest(e));
            e.printStackTrace();
        }
    }

    protected void updateStatusAndNotify(Integer status) {
        if (progress == null) {
            this.updateStatusAndNotify(status, null, null);
        } else {
            this.updateStatusAndNotify(status, progress.getProgress() * 100, progress.getCurrent());
        }
    }

    /**
     * 更新状态并且通知
     *
     * @param status      status
     * @param progress    progress
     * @param currentSize currentSize
     */
    protected void updateStatusAndNotify(Integer status, Double progress, Long currentSize) {
        Long id = record.getId();
        if (id == null) {
            return;
        }
        record.setTransferStatus(status);
        // 更新
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(id);
        update.setTransferStatus(status);
        update.setNowProgress(progress);
        update.setCurrentSize(currentSize);
        int effect = fileTransferLogDAO.updateById(update);
        log.info("sftp传输文件-更新状态 fileToken: {}, status: {}, progress: {}, currentSize: {}, effect: {}", fileToken, status, progress, currentSize, effect);
        // notify status
        transferProcessorManager.notifySessionStatusEvent(userId, machineId, fileToken, status);
    }

    /**
     * 通知进度
     *
     * @param rate     速度
     * @param current  当前位置
     * @param progress 进度
     */
    protected void notifyProgress(String rate, String current, String progress) {
        FileTransferNotifyDTO.FileTransferNotifyProgress notifyProgress = FileTransferNotifyDTO.progress(rate, current, progress);
        transferProcessorManager.notifySessionProgressEvent(userId, machineId, fileToken, notifyProgress);
    }

    /**
     * 断开连接
     */
    protected void disconnected() {
        if (executor != null) {
            executor.disconnect();
        }
    }

}
