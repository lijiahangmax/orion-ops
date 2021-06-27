package com.orion.ops.handler.sftp;

import com.alibaba.fastjson.JSON;
import com.orion.able.Executable;
import com.orion.able.Renewable;
import com.orion.able.Stoppable;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.sftp.SftpNotifyType;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.spring.SpringHolder;
import com.orion.support.progress.ByteTransferProgress;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.math.Numbers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * sftp file
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 14:58
 */
@Slf4j
public abstract class FileTransferProcessor implements Runnable, Stoppable, Executable, Renewable {

    protected static FileTransferLogDAO fileTransferLogDAO = SpringHolder.getBean("fileTransferLogDAO");

    protected static TransferProcessorManager transferProcessorManager = SpringHolder.getBean("transferProcessorManager");

    protected SessionStore sessionStore;

    @Getter
    protected FileTransferHint hint;

    @Getter
    protected SftpExecutor executor;

    @Getter
    protected FileTransferLogDO record;

    @Getter
    protected String userMachine;

    @Getter
    protected String token;

    protected boolean resume;

    protected ByteTransferProgress progress;

    protected FileTransferNotifyDTO notifyBody;

    protected FileTransferNotifyDTO.FileTransferNotifyProgress notifyProgress;

    protected volatile boolean userCancel;

    public FileTransferProcessor(FileTransferHint hint, SessionStore sessionStore) {
        this.hint = hint;
        this.sessionStore = sessionStore;
        this.token = hint.getToken();
        this.userMachine = hint.getUserId() + "_" + hint.getMachineId();
        this.notifyBody = new FileTransferNotifyDTO();
        this.notifyBody.setToken(token);
        this.notifyProgress = new FileTransferNotifyDTO.FileTransferNotifyProgress();
    }

    @Override
    public void run() {
        transferProcessorManager.addProcessor(token, this);
        try {
            if (resume) {
                // 插入记录
                this.resumeRecord();
            } else {
                // 插入记录
                this.insertRecord();
            }
            // 打开连接
            this.executor = sessionStore.getSftpExecutor(Strings.def(hint.getCharset(), Const.UTF_8));
            executor.connect();
            log.info("sftp传输文件-初始化完毕, 准备处理传输 token: {}", token);
            // 处理
            this.handler();
            log.info("sftp传输文件-传输完毕 token: {}", token);
        } catch (Exception e) {
            log.error("sftp传输文件-出现异常 token: {}, e: {}, message: {}", token, e.getClass(), e.getMessage());
            // 程序错误并非传输错误修改状态
            if (progress == null || !progress.isError()) {
                log.error("sftp传输文件-运行异常 token: {}", token, e);
                e.printStackTrace();
                this.updateStatusAndNotify(SftpTransferStatus.ERROR.getStatus());
            }
        } finally {
            transferProcessorManager.removeProcessor(token);
            this.disconnected();
        }
    }

    /**
     * 处理操作
     */
    protected abstract void handler();

    /**
     * 插入明细
     */
    protected void insertRecord() {
        this.record = new FileTransferLogDO();
        record.setUserId(hint.getUserId());
        record.setUserName(hint.getUsername());
        record.setToken(hint.getToken());
        record.setTransferType(hint.getTransferType().getType());
        record.setMachineId(hint.getMachineId());
        record.setRemoteFile(hint.getRemoteFile());
        record.setLocalFile(hint.getLocalFile());
        record.setCurrentSize(0L);
        record.setFileSize(hint.getFileSize());
        record.setNowProgress(0D);
        record.setTransferStatus(SftpTransferStatus.RUNNABLE.getStatus());
        fileTransferLogDAO.insert(record);
        // notify
        this.notifyAdd();
    }

    /**
     * 恢复下载设置明细
     */
    protected void resumeRecord() {
        this.record = fileTransferLogDAO.selectById(hint.getResumeId());
        // notify
        this.updateStatusAndNotify(SftpTransferStatus.RUNNABLE.getStatus());
    }

    /**
     * 初始化进度条
     */
    protected void initProgress(ByteTransferProgress progress) {
        this.progress = progress;
        progress.computeRate()
                .rateAcceptor(this::transferAccept)
                .callback(this::transferDoneCallback);
    }

    /**
     * 传输回调
     *
     * @param progress progress
     */
    protected void transferAccept(ByteTransferProgress progress) {
        if (progress.isDone()) {
            return;
        }
        String progressRate = Numbers.setScale(progress.getProgress() * 100, 2);
        String transferRate = Files1.getSize(progress.getNowRate());
        String transferCurrent = Files1.getSize(progress.getCurrent());
        // debug
        log.debug(transferCurrent + " " + progressRate + "% " + transferRate + "/s");
        // notify
        notifyProgress.setCurrent(transferCurrent);
        notifyProgress.setProgress(progressRate);
        notifyProgress.setRate(transferRate);
        this.notifyProgress();
    }

    /**
     * 传输完成回调
     *
     * @param progress progress
     */
    protected void transferDoneCallback(ByteTransferProgress progress) {
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(record.getId());
        if (progress.isError()) {
            // 非用户取消更新状态
            if (!userCancel) {
                this.updateStatusAndNotify(SftpTransferStatus.ERROR.getStatus());
            }
        } else {
            this.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, progress.getEnd());
        }
    }

    @Override
    public void stop() {
        log.info("sftp传输文件-用户暂停 token: {}", token);
        this.userCancel = true;
        this.updateStatusAndNotify(SftpTransferStatus.PAUSE.getStatus());
        this.disconnected();
    }

    protected void updateStatusAndNotify(Integer status) {
        if (progress == null) {
            this.updateStatusAndNotify(status, 0D, 0L);
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
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(id);
        update.setNowProgress(progress);
        update.setTransferStatus(status);
        update.setCurrentSize(currentSize);
        int effect = fileTransferLogDAO.updateById(update);
        log.info("sftp传输文件-更新状态 id: {}, token: {}, status: {}, progress: {}, currentSize: {}, effect: {}",
                id, token, status, progress, currentSize, effect);
        // notify
        this.notifyChangeStatus(status);
    }

    /**
     * 断开连接
     */
    protected void disconnected() {
        if (executor != null) {
            executor.disconnectChannel();
        }
    }

    /**
     * 通知添加
     */
    protected void notifyAdd() {
        notifyBody.setType(SftpNotifyType.ADD.getType());
        notifyBody.setBody(JSON.toJSONString(record));
        transferProcessorManager.notifySession(userMachine, notifyBody);
    }

    /**
     * 通知进度
     */
    protected void notifyProgress() {
        notifyBody.setType(SftpNotifyType.PROGRESS.getType());
        notifyBody.setBody(JSON.toJSONString(notifyProgress));
        transferProcessorManager.notifySession(userMachine, notifyBody);
    }

    /**
     * 通知更新状态
     */
    protected void notifyChangeStatus(Integer status) {
        notifyBody.setType(SftpNotifyType.CHANGE_STATUS.getType());
        notifyBody.setBody(status);
        transferProcessorManager.notifySession(userMachine, notifyBody);
    }

}
