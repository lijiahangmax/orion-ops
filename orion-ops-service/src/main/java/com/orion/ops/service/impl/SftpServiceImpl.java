package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpPackageType;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.FileTransferNotifyDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileDetailVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.handler.sftp.IFileTransferProcessor;
import com.orion.ops.handler.sftp.SftpBasicExecutorHolder;
import com.orion.ops.handler.sftp.TransferProcessorManager;
import com.orion.ops.handler.sftp.hint.FileTransferHint;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.remote.channel.sftp.SftpFile;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * sftp实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:41
 */
@Service("sftpService")
public class SftpServiceImpl implements SftpService {

    @Resource
    private TransferProcessorManager transferProcessorManager;

    @Resource
    private SftpBasicExecutorHolder sftpBasicExecutorHolder;

    @Resource
    private FileTransferLogDAO fileTransferLogDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public FileOpenVO open(Long machineId) {
        // 获取当前用户
        Long userId = Currents.getUserId();
        // 生成token
        String sessionToken = this.generatorSessionToken(userId, machineId);
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(sessionToken);
        // 查询列表
        String path = executor.getHome();
        FileListVO list = this.list(path, 0, executor);
        // 返回数据
        FileOpenVO openVO = new FileOpenVO();
        openVO.setSessionToken(sessionToken);
        openVO.setHome(path);
        openVO.setCharset(executor.getCharset());
        openVO.setPath(list.getPath());
        openVO.setFiles(list.getFiles());
        return openVO;
    }

    @Override
    public FileListVO list(FileListRequest request) {
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        return this.list(request.getPath(), request.getAll(), executor);
    }

    private FileListVO list(String path, int all, SftpExecutor executor) {
        synchronized (executor) {
            List<SftpFile> fileList;
            if (all == 0) {
                // 不查询隐藏文件
                fileList = executor.listFilesFilter(path, f -> !f.getName().startsWith("."), false, true);
            } else {
                // 查询隐藏文件
                fileList = executor.ll(path);
            }
            // 返回
            FileListVO fileListVO = new FileListVO();
            List<FileDetailVO> files = Converts.toList(fileList, FileDetailVO.class);
            fileListVO.setPath(path);
            fileListVO.setFiles(files);
            return fileListVO;
        }
    }

    @Override
    public FileListVO listDir(FileListRequest request) {
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            String path = request.getPath();
            List<SftpFile> fileList = executor.listDirs(path);
            // 返回
            FileListVO fileListVO = new FileListVO();
            List<FileDetailVO> files = Converts.toList(fileList, FileDetailVO.class);
            fileListVO.setPath(path);
            fileListVO.setFiles(files);
            return fileListVO;
        }
    }

    @Override
    public String mkdir(FileMkdirRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.mkdirs(path);
        }
        return path;
    }

    @Override
    public String touch(FileTouchRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.touch(path);
        }
        return path;
    }

    @Override
    public void truncate(FileTruncateRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.truncate(path);
        }
    }

    @Override
    public String move(FileMoveRequest request) {
        String source = Files1.getPath(request.getSource());
        String target = Files1.getPath(request.getTarget());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.mv(source, target);
        }
        return target;
    }

    @Override
    public void remove(FileRemoveRequest request) {
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            request.getPaths().stream()
                    .map(Files1::getPath)
                    .forEach(executor::rm);
        }
    }

    @Override
    public String chmod(FileChmodRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.chmod(path, request.getPermission());
            return Files1.permission10toString(request.getPermission());
        }
    }

    @Override
    public void chown(FileChownRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.chown(path, request.getUid());
        }
    }

    @Override
    public void changeGroup(FileChangeGroupRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            executor.chgrp(path, request.getGid());
        }
    }

    @Override
    public List<String> checkFilePresent(FilePresentCheckRequest request) {
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        synchronized (executor) {
            return request.getNames().stream()
                    .filter(Strings::isNotBlank)
                    .filter(s -> executor.isExist(Files1.getPath(request.getPath(), s)))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getUploadAccessToken(String sessionToken) {
        Long[] tokenInfo = this.getTokenInfo(sessionToken);
        Long userId = Currents.getUserId();
        Valid.isTrue(tokenInfo[0].equals(userId), MessageConst.SESSION_EXPIRE);
        // 生成accessToken
        String accessToken = UUIds.random32();
        String key = Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, accessToken);
        redisTemplate.opsForValue().set(key, userId + "_" + tokenInfo[1],
                KeyConst.SFTP_UPLOAD_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return accessToken;
    }

    @Override
    public Long checkUploadAccessToken(String accessToken) {
        // 获取缓存
        String key = Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, accessToken);
        String value = redisTemplate.opsForValue().get(key);
        Valid.notBlank(value, MessageConst.TOKEN_EXPIRE);
        // 解析缓存
        Long[] valueInfo = Arrays1.mapper(Objects.requireNonNull(value).split("_"), Long[]::new, Long::valueOf);
        Valid.isTrue(valueInfo[0].equals(Currents.getUserId()), MessageConst.TOKEN_EXPIRE);
        // 删除缓存
        redisTemplate.delete(key);
        return valueInfo[1];
    }

    @Override
    public void upload(Long machineId, List<FileUploadRequest> requestFiles) {
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 初始化上传信息
        List<FileTransferLogDO> uploadFiles = Lists.newList();
        for (FileUploadRequest requestFile : requestFiles) {
            // 插入明细
            FileTransferLogDO upload = new FileTransferLogDO();
            upload.setUserId(userId);
            upload.setUserName(user.getUsername());
            upload.setFileToken(requestFile.getFileToken());
            upload.setTransferType(SftpTransferType.UPLOAD.getType());
            upload.setMachineId(machineId);
            upload.setRemoteFile(requestFile.getRemotePath());
            upload.setLocalFile(requestFile.getLocalPath());
            upload.setCurrentSize(0L);
            upload.setFileSize(requestFile.getSize());
            upload.setNowProgress(0D);
            upload.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            uploadFiles.add(upload);
            fileTransferLogDAO.insert(upload);
            // 通知添加
            transferProcessorManager.notifySessionAddEvent(userId, machineId, upload.getFileToken(), upload);
        }
        // 提交上传任务
        for (FileTransferLogDO uploadFile : uploadFiles) {
            IFileTransferProcessor.of(FileTransferHint.transfer(uploadFile)).exec();
        }
    }

    @Override
    public void download(FileDownloadRequest request) {
        // 获取token信息
        Long machineId = this.getMachineId(request.getSessionToken());
        SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(request.getSessionToken());
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 定义文件转换器
        Function<SftpFile, FileTransferLogDO> convert = file -> {
            // 本地保存路径
            String fileToken = ObjectIds.next();
            // 设置传输信息
            FileTransferLogDO download = new FileTransferLogDO();
            download.setUserId(userId);
            download.setUserName(user.getUsername());
            download.setFileToken(fileToken);
            download.setTransferType(SftpTransferType.DOWNLOAD.getType());
            download.setMachineId(machineId);
            download.setRemoteFile(file.getPath());
            download.setLocalFile(PathBuilders.getSftpDownloadFilePath(fileToken));
            download.setCurrentSize(0L);
            download.setFileSize(file.getSize());
            download.setNowProgress(0D);
            download.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            return download;
        };
        // 初始化下载信息
        List<FileTransferLogDO> downloadFiles = Lists.newList();
        for (String path : request.getPaths()) {
            SftpFile file = executor.getFile(path);
            Valid.notNull(file, Strings.format(MessageConst.FILE_NOT_FOUND, path));
            // 如果是文件夹则递归所有文件
            if (file.isDirectory()) {
                List<SftpFile> childFiles = executor.listFiles(path, true, false);
                childFiles.forEach(f -> downloadFiles.add(convert.apply(f)));
            } else {
                downloadFiles.add(convert.apply(file));
            }
        }
        for (FileTransferLogDO downloadFile : downloadFiles) {
            fileTransferLogDAO.insert(downloadFile);
            // 通知添加
            transferProcessorManager.notifySessionAddEvent(userId, machineId, downloadFile.getFileToken(), downloadFile);
        }
        // 提交下载任务
        for (FileTransferLogDO downloadFile : downloadFiles) {
            IFileTransferProcessor.of(FileTransferHint.transfer(downloadFile)).exec();
        }
    }

    @Override
    public void transferPause(String fileToken) {
        // 获取请求文件
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        // 判断状态是否为进行中
        Integer status = transferLog.getTransferStatus();
        Valid.isTrue(SftpTransferStatus.WAIT.getStatus().equals(status)
                || SftpTransferStatus.RUNNABLE.getStatus().equals(status), MessageConst.INVALID_STATUS);
        // 获取执行器
        IFileTransferProcessor processor = transferProcessorManager.getProcessor(fileToken);
        if (processor != null) {
            // 执行器不为空则终止
            processor.stop();
        } else {
            Integer changeStatus;
            if (SftpTransferType.PACKAGE.getType().equals(transferLog.getTransferType())) {
                changeStatus = SftpTransferStatus.CANCEL.getStatus();
            } else {
                changeStatus = SftpTransferStatus.PAUSE.getStatus();
            }
            // 修改状态
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(changeStatus);
            fileTransferLogDAO.updateById(update);
            // 通知状态
            transferProcessorManager.notifySessionStatusEvent(transferLog.getUserId(), transferLog.getMachineId(), transferLog.getFileToken(), changeStatus);
        }
    }

    @Override
    public void transferResume(String fileToken) {
        // 获取请求文件
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        Long machineId = transferLog.getMachineId();
        // 判断状态是否为暂停
        Valid.eq(SftpTransferStatus.PAUSE.getStatus(), transferLog.getTransferStatus(), MessageConst.INVALID_STATUS);
        this.transferResumeRetry(transferLog, machineId);
    }

    @Override
    public void transferRetry(String fileToken) {
        // 获取请求文件
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        Long machineId = transferLog.getMachineId();
        // 判断状态是否为失败
        Valid.eq(SftpTransferStatus.ERROR.getStatus(), transferLog.getTransferStatus(), MessageConst.INVALID_STATUS);
        this.transferResumeRetry(transferLog, machineId);
    }

    /**
     * 传输恢复/传输重试
     *
     * @param transferLog transferLog
     * @param machineId   machineId
     */
    private void transferResumeRetry(FileTransferLogDO transferLog, Long machineId) {
        // 修改状态为等待
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(transferLog.getId());
        update.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
        fileTransferLogDAO.updateById(update);
        // 通知状态
        transferProcessorManager.notifySessionStatusEvent(transferLog.getUserId(), machineId, transferLog.getFileToken(), SftpTransferStatus.WAIT.getStatus());
        // 提交下载
        IFileTransferProcessor.of(FileTransferHint.transfer(transferLog)).exec();
    }

    @Override
    public void transferReUpload(String fileToken) {
        this.transferReTransfer(fileToken, true);
    }

    @Override
    public void transferReDownload(String fileToken) {
        this.transferReTransfer(fileToken, false);
    }

    /**
     * 重新传输
     */
    private void transferReTransfer(String fileToken, boolean isUpload) {
        // 获取请求文件
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        Long machineId = transferLog.getMachineId();
        // 暂停
        IFileTransferProcessor processor = transferProcessorManager.getProcessor(transferLog.getFileToken());
        if (processor != null) {
            processor.stop();
        }
        if (isUpload) {
            // 删除远程文件
            SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(machineId);
            SftpFile file = executor.getFile(transferLog.getRemoteFile());
            if (file != null) {
                executor.rmFile(transferLog.getRemoteFile());
            }
        } else {
            // 删除本地文件
            String loacalPath = Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue(), transferLog.getLocalFile());
            Files1.delete(loacalPath);
        }
        // 修改进度
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(transferLog.getId());
        update.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
        update.setNowProgress(0D);
        update.setCurrentSize(0L);
        fileTransferLogDAO.updateById(update);
        // 通知进度
        FileTransferNotifyDTO.FileTransferNotifyProgress progress = FileTransferNotifyDTO.progress(Strings.EMPTY, Files1.getSize(transferLog.getFileSize()), "0");
        transferProcessorManager.notifySessionProgressEvent(transferLog.getUserId(), machineId, transferLog.getFileToken(), progress);
        // 通知状态
        transferProcessorManager.notifySessionStatusEvent(transferLog.getUserId(), machineId, transferLog.getFileToken(), SftpTransferStatus.WAIT.getStatus());
        // 提交下载
        IFileTransferProcessor.of(FileTransferHint.transfer(transferLog)).exec();
    }

    @Override
    public void transferPauseAll(String sessionToken) {
        // 获取token信息
        Long machineId = this.getMachineId(sessionToken);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .in(FileTransferLogDO::getTransferStatus, SftpTransferStatus.WAIT.getStatus(), SftpTransferStatus.RUNNABLE.getStatus())
                .in(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType(), SftpTransferType.DOWNLOAD.getType());
        List<FileTransferLogDO> transferLogs = fileTransferLogDAO.selectList(wrapper);
        for (FileTransferLogDO transferLog : transferLogs) {
            // 修改状态为暂停
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(SftpTransferStatus.PAUSE.getStatus());
            fileTransferLogDAO.updateById(update);
            // 通知状态
            transferProcessorManager.notifySessionStatusEvent(transferLog.getUserId(), machineId, transferLog.getFileToken(), SftpTransferStatus.PAUSE.getStatus());
        }
        // 获取执行器暂停
        for (FileTransferLogDO transferLog : transferLogs) {
            IFileTransferProcessor processor = transferProcessorManager.getProcessor(transferLog.getFileToken());
            if (processor != null) {
                processor.stop();
            }
        }
    }

    @Override
    public void transferResumeAll(String sessionToken) {
        // 获取token信息
        Long machineId = this.getMachineId(sessionToken);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.PAUSE.getStatus())
                .in(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType(), SftpTransferType.DOWNLOAD.getType());
        List<FileTransferLogDO> transferLogs = fileTransferLogDAO.selectList(wrapper);
        this.transferResumeRetryAll(transferLogs, machineId);
    }

    @Override
    public void transferRetryAll(String sessionToken) {
        // 获取token信息
        Long machineId = this.getMachineId(sessionToken);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.ERROR.getStatus())
                .in(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType(), SftpTransferType.DOWNLOAD.getType());
        List<FileTransferLogDO> transferLogs = fileTransferLogDAO.selectList(wrapper);
        this.transferResumeRetryAll(transferLogs, machineId);
    }

    /**
     * 传输恢复/传输重试全部
     *
     * @param transferLogs transferLogs
     * @param machineId    machineId
     */
    private void transferResumeRetryAll(List<FileTransferLogDO> transferLogs, Long machineId) {
        for (FileTransferLogDO transferLog : transferLogs) {
            // 修改状态为等待
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            fileTransferLogDAO.updateById(update);
            // 通知状态
            transferProcessorManager.notifySessionStatusEvent(transferLog.getUserId(), machineId, transferLog.getFileToken(), SftpTransferStatus.WAIT.getStatus());
        }
        // 提交传输
        for (FileTransferLogDO transferLog : transferLogs) {
            IFileTransferProcessor.of(FileTransferHint.transfer(transferLog)).exec();
        }
    }

    @Override
    public void transferRemove(String fileToken) {
        // 获取请求文件
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        // 如果是进行中则需要取消任务
        if (SftpTransferStatus.RUNNABLE.getStatus().equals(transferLog.getTransferStatus())) {
            IFileTransferProcessor processor = transferProcessorManager.getProcessor(fileToken);
            if (processor != null) {
                processor.stop();
            }
        }
        // 修改状态
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(transferLog.getId());
        update.setShowType(Const.DISABLE);
        fileTransferLogDAO.updateById(update);
    }

    @Override
    public List<FileTransferLogVO> transferList(Long machineId) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .in(FileTransferLogDO::getTransferType,
                        SftpTransferType.UPLOAD.getType(),
                        SftpTransferType.DOWNLOAD.getType(),
                        SftpTransferType.PACKAGE.getType())
                .orderByDesc(FileTransferLogDO::getId);
        List<FileTransferLogDO> logList = fileTransferLogDAO.selectList(wrapper);
        return Converts.toList(logList, FileTransferLogVO.class);
    }

    @Override
    public Integer transferClear(Long machineId) {
        FileTransferLogDO update = new FileTransferLogDO();
        update.setShowType(Const.DISABLE);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .ne(FileTransferLogDO::getTransferStatus, SftpTransferStatus.RUNNABLE.getStatus());
        return fileTransferLogDAO.update(update, wrapper);
    }

    @Override
    public void transferPackage(String sessionToken, SftpPackageType packageType) {
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 获取token信息
        Long machineId = this.getMachineId(sessionToken);
        // 查询要打包的文件
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, userId)
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.FINISH.getStatus())
                .eq(FileTransferLogDO::getShowType, Const.ENABLE);
        switch (packageType) {
            case UPLOAD:
                wrapper.eq(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType());
                break;
            case DOWNLOAD:
                wrapper.eq(FileTransferLogDO::getTransferType, SftpTransferType.DOWNLOAD.getType());
                break;
            case ALL:
                wrapper.in(FileTransferLogDO::getTransferType,
                        SftpTransferType.UPLOAD.getType(),
                        SftpTransferType.DOWNLOAD.getType(),
                        SftpTransferType.PACKAGE.getType());
                break;
            default:
                break;
        }
        wrapper.orderByDesc(FileTransferLogDO::getId);
        List<FileTransferLogDO> logList = fileTransferLogDAO.selectList(wrapper);
        Valid.notEmpty(logList, MessageConst.TRANSFER_ITEM_EMPTY);
        // 文件名称
        String fileName = Files1.getFileName(logList.get(0).getRemoteFile()) + "等" + logList.size() + "个文件.zip";
        String fileToken = ObjectIds.next();
        // 文件大小
        long fileSize = logList.stream().mapToLong(FileTransferLogDO::getFileSize).sum();
        // 插入传输记录
        FileTransferLogDO packageRecord = new FileTransferLogDO();
        packageRecord.setUserId(userId);
        packageRecord.setUserName(user.getUsername());
        packageRecord.setFileToken(fileToken);
        packageRecord.setTransferType(SftpTransferType.PACKAGE.getType());
        packageRecord.setMachineId(machineId);
        packageRecord.setRemoteFile(fileName);
        packageRecord.setLocalFile(PathBuilders.getSftpPackageFilePath(fileToken));
        packageRecord.setCurrentSize(0L);
        packageRecord.setFileSize(fileSize);
        packageRecord.setNowProgress(0D);
        packageRecord.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
        fileTransferLogDAO.insert(packageRecord);
        // 通知添加
        transferProcessorManager.notifySessionAddEvent(userId, machineId, packageRecord.getFileToken(), packageRecord);
        // 提交打包任务
        IFileTransferProcessor.of(FileTransferHint.packaged(packageRecord, logList)).exec();
    }

    @Override
    public FileTransferLogDO getDownloadFilePath(Long id) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(!Currents.isAdministrator(), FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.FINISH.getStatus())
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .eq(FileTransferLogDO::getId, id);
        FileTransferLogDO transferLog = fileTransferLogDAO.selectOne(wrapper);
        if (transferLog == null) {
            return null;
        }
        transferLog.setLocalFile(Files1.getPath(MachineEnvAttr.SWAP_PATH.getValue(), transferLog.getLocalFile()));
        return transferLog;
    }

    @Override
    public Long getMachineId(String sessionToken) {
        return this.getTokenInfo(sessionToken)[1];
    }

    @Override
    public Long[] getTokenInfo(String sessionToken) {
        Valid.notBlank(sessionToken, MessageConst.TOKEN_EMPTY);
        String key = Strings.format(KeyConst.SFTP_SESSION_TOKEN, sessionToken);
        String value = redisTemplate.opsForValue().get(key);
        Valid.notBlank(value, MessageConst.SESSION_EXPIRE);
        return Arrays1.mapper(Objects.requireNonNull(value).split("_"), Long[]::new, Long::valueOf);
    }

    /**
     * 生成sessionToken
     *
     * @param userId    userId
     * @param machineId 机器id
     * @return sessionToken
     */
    private String generatorSessionToken(Long userId, Long machineId) {
        // 生成token
        String sessionToken = UUIds.random15();
        // 设置缓存
        String key = Strings.format(KeyConst.SFTP_SESSION_TOKEN, sessionToken);
        redisTemplate.opsForValue().set(key, userId + "_" + machineId,
                KeyConst.SFTP_SESSION_EXPIRE, TimeUnit.SECONDS);
        return sessionToken;
    }

    /**
     * 查询传输日志
     *
     * @param fileToken fileToken
     * @return FileTransferLogDO
     */
    private FileTransferLogDO getTransferLogByToken(String fileToken) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getFileToken, fileToken)
                .eq(FileTransferLogDO::getUserId, Currents.getUserId());
        return fileTransferLogDAO.selectOne(wrapper);
    }

}
