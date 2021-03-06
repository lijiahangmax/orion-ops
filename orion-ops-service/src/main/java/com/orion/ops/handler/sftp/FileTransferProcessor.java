package com.orion.ops.handler.sftp;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.sftp.SftpNotifyType;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.spring.SpringHolder;
import com.orion.support.progress.ByteTransferProgress;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import com.orion.utils.json.Jsons;
import com.orion.utils.math.Numbers;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * sftp 传输文件基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 14:58
 */
@Slf4j
public abstract class FileTransferProcessor implements IFileTransferProcessor {

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
    protected String fileToken;

    protected boolean resume;

    protected ByteTransferProgress progress;

    protected FileTransferNotifyDTO notifyBody;

    protected FileTransferNotifyDTO.FileTransferNotifyProgress notifyProgress;

    protected volatile boolean userCancel;

    public FileTransferProcessor(FileTransferHint hint, SessionStore sessionStore) {
        this.hint = hint;
        this.sessionStore = sessionStore;
        this.fileToken = hint.getFileToken();
        this.userMachine = hint.getUserId() + "_" + hint.getMachineId();
        this.notifyBody = new FileTransferNotifyDTO();
        this.notifyBody.setFileToken(fileToken);
        this.notifyProgress = new FileTransferNotifyDTO.FileTransferNotifyProgress();
    }

    @Override
    public void run() {
        transferProcessorManager.addProcessor(fileToken, this);
        try {
            if (resume) {
                this.resumeRecord();
            } else {
                this.insertRecord();
            }
            // 打开连接
            this.executor = sessionStore.getSftpExecutor(Strings.def(hint.getCharset(), Const.UTF_8));
            executor.connect();
            log.info("sftp传输文件-初始化完毕, 准备处理传输 fileToken: {}", fileToken);
            // 处理
            this.handler();
            log.info("sftp传输文件-传输完毕 fileToken: {}", fileToken);
        } catch (Exception e) {
            log.error("sftp传输文件-出现异常 fileToken: {}, e: {}, message: {}", fileToken, e.getClass().getName(), e.getMessage());
            // 程序错误并非传输错误修改状态
            if (progress == null || !progress.isError()) {
                log.error("sftp传输文件-运行异常 fileToken: {}", fileToken, e);
                e.printStackTrace();
                this.updateStatusAndNotify(SftpTransferStatus.ERROR.getStatus());
            }
        } finally {
            transferProcessorManager.removeProcessor(fileToken);
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
        record.setFileToken(hint.getFileToken());
        record.setTransferType(hint.getTransferType().getType());
        record.setMachineId(hint.getMachineId());
        record.setRemoteFile(hint.getRemoteFile());
        record.setLocalFile(hint.getLocalFile());
        record.setCurrentSize(0L);
        record.setFileSize(hint.getFileSize());
        record.setNowProgress(0D);
        record.setTransferStatus(SftpTransferStatus.RUNNABLE.getStatus());
        fileTransferLogDAO.insert(record);
        // notify add
        this.notifyAdd();
    }

    /**
     * 恢复下载设置明细
     */
    protected void resumeRecord() {
        this.record = fileTransferLogDAO.selectById(hint.getResumeId());
        // notify status
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
        // log.info(transferCurrent + " " + progressRate + "% " + transferRate + "/s");
        // notify progress
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
            String transferCurrent = Files1.getSize(progress.getCurrent());
            String transferRate = Files1.getSize(progress.getNowRate());
            notifyProgress.setCurrent(transferCurrent);
            notifyProgress.setProgress(100 + "");
            notifyProgress.setRate(transferRate);
            // notify progress
            this.notifyProgress();
            // notify status
            this.updateStatusAndNotify(SftpTransferStatus.FINISH.getStatus(), 100D, progress.getEnd());
        }
    }

    @Override
    public void stop() {
        log.info("sftp传输文件-用户暂停 fileToken: {}", fileToken);
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
        record.setTransferStatus(status);
        // 更新
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(id);
        update.setNowProgress(progress);
        update.setTransferStatus(status);
        update.setCurrentSize(currentSize);
        int effect = fileTransferLogDAO.updateById(update);
        log.info("sftp传输文件-更新状态 id: {}, fileToken: {}, status: {}, progress: {}, currentSize: {}, effect: {}",
                id, fileToken, status, progress, currentSize, effect);
        // notify status
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
        notifyBody.setBody(Jsons.toJsonWriteNull(Converts.to(record, FileTransferLogVO.class)));
        transferProcessorManager.notifySession(userMachine, notifyBody);
    }

    /**
     * 通知进度
     */
    protected void notifyProgress() {
        notifyBody.setType(SftpNotifyType.PROGRESS.getType());
        notifyBody.setBody(Jsons.toJsonWriteNull(notifyProgress));
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
