package com.orion.ops.handler.sftp.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.ops.handler.sftp.IFileTransferProcessor;
import com.orion.ops.handler.sftp.TransferProcessorManager;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.io.compress.CompressTypeEnum;
import com.orion.utils.io.compress.FileCompressor;
import com.orion.utils.math.Numbers;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件打包处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/6 17:04
 */
@Slf4j
public class PackageFileProcessor implements IFileTransferProcessor {

    protected static FileTransferLogDAO fileTransferLogDAO = SpringHolder.getBean("fileTransferLogDAO");

    protected static TransferProcessorManager transferProcessorManager = SpringHolder.getBean("transferProcessorManager");

    /**
     * 打包文件
     */
    private FileTransferLogDO packageFile;

    /**
     * 文件列表
     */
    private List<FileTransferLogDO> fileList;

    /**
     * 文件名映射
     */
    private Map<String, FileTransferLogDO> nameMapping;

    /**
     * 当前大小
     */
    private volatile AtomicLong currentSize;

    /**
     * 文件总大小
     */
    private long totalSize;

    /**
     * 文件压缩器
     */
    private FileCompressor compressor;

    private Long userId;

    private Long machineId;

    private String fileToken;

    private volatile boolean userCancel;

    private volatile boolean done;

    public PackageFileProcessor(FileTransferLogDO packageFile, List<FileTransferLogDO> fileList) {
        this.packageFile = packageFile;
        this.fileList = fileList;
        this.fileToken = packageFile.getFileToken();
        this.currentSize = new AtomicLong();
        this.totalSize = packageFile.getFileSize();
        this.userId = packageFile.getUserId();
        this.machineId = packageFile.getMachineId();
    }

    @Override
    public void exec() {
        String localFile = packageFile.getLocalFile();
        String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + localFile);
        log.info("sftp文件打包-提交任务 fileToken: {} machineId: {}, local: {}, remote: {}, record: {}, fileList: {}",
                fileToken, machineId, localAbsolutePath, packageFile.getRemoteFile(),
                JSON.toJSONString(packageFile), JSON.toJSONString(fileList));
        Threads.start(this, SchedulerPools.SFTP_PACKAGE_SCHEDULER);
    }

    @Override
    public void run() {
        // 判断是否可以传输
        FileTransferLogDO fileTransferLog = fileTransferLogDAO.selectById(packageFile.getId());
        if (Const.DISABLE.equals(fileTransferLog.getShowType())
                || !SftpTransferStatus.WAIT.getStatus().equals(fileTransferLog.getTransferStatus())) {
            return;
        }
        transferProcessorManager.addProcessor(fileToken, this);
        try {
            // 通知状态runnable
            this.updateStatus(SftpTransferStatus.RUNNABLE);
            // 初始化压缩器
            this.compressor = CompressTypeEnum.ZIP.compressor().get();
            String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + fileTransferLog.getLocalFile());
            compressor.setAbsoluteCompressPath(localAbsolutePath);
            compressor.compressNotify(this::notifyProgress);
            // 添加压缩文件
            this.initCompressFiles();
            // 添加压缩清单
            this.initCompressFileRaw();
            // 二次检查状态 防止在添加文件过程中取消或者删除
            fileTransferLog = fileTransferLogDAO.selectById(packageFile.getId());
            if (Const.DISABLE.equals(fileTransferLog.getShowType())
                    || !SftpTransferStatus.RUNNABLE.getStatus().equals(fileTransferLog.getTransferStatus())) {
                return;
            }
            // 开始压缩
            this.compressor.compress();
            // 传输完成通知
            this.updateStatus(SftpTransferStatus.FINISH);
        } catch (Exception e) {
            log.error("sftp压缩文件-出现异常 fileToken: {}, e: {}, message: {}", fileToken, e.getClass().getName(), e.getMessage());
            // 程序错误并非传输错误修改状态
            if (!userCancel) {
                log.error("sftp传输文件-运行异常 fileToken: {}", fileToken, e);
                this.updateStatus(SftpTransferStatus.ERROR);
            }
            e.printStackTrace();
        } finally {
            this.done = true;
            transferProcessorManager.removeProcessor(fileToken);
        }
    }

    @Override
    public void stop() {
        log.info("sftp传输打包-用户取消 fileToken: {}", fileToken);
        this.userCancel = true;
        // 修改状态为已取消
        this.updateStatus(SftpTransferStatus.CANCEL);
        // 取消
        if (compressor != null) {
            Streams.close(compressor.getCloseable());
        }
    }

    /**
     * 初始化压缩文件
     */
    private void initCompressFiles() {
        this.nameMapping = new LinkedHashMap<>();
        for (int i = 0; i < fileList.size(); i++) {
            FileTransferLogDO fileLog = fileList.get(i);
            String remoteFile = fileLog.getRemoteFile();
            String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + fileLog.getLocalFile());
            if (!Files1.isFile(new File(localAbsolutePath))) {
                continue;
            }
            // 添加mapping
            String remoteFileName;
            if (nameMapping.containsKey(remoteFile)) {
                remoteFileName = remoteFile + "_" + (i + 1);
            } else {
                remoteFileName = remoteFile;
            }
            nameMapping.put(remoteFileName, fileLog);
            compressor.addFile(remoteFileName, localAbsolutePath);
        }
    }

    /**
     * 添加压缩文件清单到压缩列表
     */
    private void initCompressFileRaw() {
        // 设置文件清单
        List<String> compressFileRaw = new ArrayList<>();
        for (FileTransferLogDO fileLog : fileList) {
            String remoteFile = fileLog.getRemoteFile();
            String label = SftpTransferType.of(fileLog.getTransferType()).getLabel();
            String localAbsolutePath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue() + "/" + fileLog.getLocalFile());
            String status = Files1.isFile(localAbsolutePath) ? "成功" : "未找到文件";
            // 添加raw
            compressFileRaw.add(remoteFile + " " + label + " " + status);
        }
        // 设置文件清单文件
        String compressRawListFile = String.join("\n", compressFileRaw) + "\n";
        InputStream compressRawListStream = Streams.toInputStream(compressRawListFile);
        compressor.addFile(Const.COMPRESS_LIST_FILE, compressRawListStream);
    }

    /**
     * 通知状态
     *
     * @param status status
     */
    private void updateStatus(SftpTransferStatus status) {
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(packageFile.getId());
        update.setTransferStatus(status.getStatus());
        if (SftpTransferStatus.FINISH.equals(status)) {
            update.setCurrentSize(packageFile.getFileSize());
            update.setNowProgress(100D);
        }
        int effect = fileTransferLogDAO.updateById(update);
        log.info("sftp传输压缩-更新状态 fileToken: {}, status: {}, effect: {}", fileToken, status, effect);
        if (SftpTransferStatus.FINISH.equals(status)) {
            // 通知进度
            FileTransferNotifyDTO.FileTransferNotifyProgress notifyProgress = FileTransferNotifyDTO.progress(Strings.EMPTY, Files1.getSize(totalSize), "100");
            transferProcessorManager.notifySessionProgressEvent(userId, machineId, fileToken, notifyProgress);
        }
        // 通知状态
        transferProcessorManager.notifySessionStatusEvent(userId, machineId, fileToken, status.getStatus());
    }

    /**
     * 通知进度
     *
     * @param name name
     */
    private void notifyProgress(String name) {
        if (done) {
            return;
        }
        FileTransferLogDO compressedFile = nameMapping.get(name);
        if (compressedFile == null) {
            return;
        }
        // 计算进度
        long curr = currentSize.addAndGet(compressedFile.getFileSize());
        double progress = this.getProgress();
        String progressRate = Numbers.setScale(progress, 2);
        // 更新进度
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(packageFile.getId());
        update.setCurrentSize(curr);
        update.setNowProgress(progress);
        fileTransferLogDAO.updateById(update);
        // 通知进度
        FileTransferNotifyDTO.FileTransferNotifyProgress notifyProgress = FileTransferNotifyDTO.progress(Strings.EMPTY, Files1.getSize(curr), progressRate);
        transferProcessorManager.notifySessionProgressEvent(userId, machineId, fileToken, notifyProgress);
    }

    /**
     * 获取当前进度
     *
     * @return 当前进度
     */
    protected double getProgress() {
        if (totalSize == 0) {
            return 0;
        }
        return ((double) currentSize.get() / (double) totalSize) * 100;
    }

}
