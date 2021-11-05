package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpNotifyType;
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
import com.orion.ops.handler.sftp.TransferProcessorManager;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.remote.channel.sftp.SftpFile;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import com.orion.utils.json.Jsons;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private TransferProcessorManager transferProcessorManager;

    @Resource
    private FileTransferLogDAO fileTransferLogDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 基本操作的executor 不包含(upload, download)
     */
    private final Map<Long, SftpExecutor> basicExecutorHolder = Maps.newCurrentHashMap();

    @Override
    public FileOpenVO open(Long machineId) {
        // 获取当前用户
        Long userId = Currents.getUserId();
        SftpExecutor executor = this.getBasicExecutor(machineId);
        // 生成token
        String sessionToken = this.generatorSessionToken(userId, machineId);
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
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        return this.list(request.getPath(), request.getAll(), executor);
    }

    private FileListVO list(String path, int all, SftpExecutor executor) {
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

    @Override
    public FileListVO listDir(FileListRequest request) {
        List<SftpFile> fileList;
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        String path = request.getPath();
        if (request.getAll() == 0) {
            // 不查询隐藏文件
            fileList = executor.listFilesFilter(path, f -> !f.getName().startsWith(".") && f.isDirectory(), false, true);
        } else {
            // 查询隐藏文件
            fileList = executor.listDirs(path);
        }
        // 返回
        FileListVO fileListVO = new FileListVO();
        List<FileDetailVO> files = Converts.toList(fileList, FileDetailVO.class);
        fileListVO.setPath(path);
        fileListVO.setFiles(files);
        return fileListVO;
    }

    @Override
    public String mkdir(FileMkdirRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.mkdirs(path);
        Valid.sftp(r);
        return path;
    }

    @Override
    public String touch(FileTouchRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.touch(path);
        Valid.sftp(r);
        return path;
    }

    @Override
    public void truncate(FileTruncateRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.truncate(path);
        Valid.sftp(r);
    }

    @Override
    public String move(FileMoveRequest request) {
        String source = Files1.getPath(request.getSource());
        String target = Files1.getPath(request.getTarget());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.mv(source, target);
        Valid.sftp(r);
        return target;
    }

    @Override
    public void remove(FileRemoveRequest request) {
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        request.getPaths().stream()
                .map(Files1::getPath)
                .forEach(executor::rm);
    }

    @Override
    public String chmod(FileChmodRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.chmod(path, request.getPermission());
        Valid.sftp(r);
        return Files1.permission10toString(request.getPermission());
    }

    @Override
    public void chown(FileChownRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.chown(path, request.getUid());
        Valid.sftp(r);
    }

    @Override
    public void changeGroup(FileChangeGroupRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        boolean r = executor.chgrp(path, request.getGid());
        Valid.sftp(r);
    }

    @Override
    public List<String> checkFilePresent(FilePresentCheckRequest request) {
        SftpExecutor executor = this.getBasicExecutor(this.getMachineId(request.getSessionToken()));
        return request.getNames().stream()
                .filter(Strings::isNotBlank)
                .filter(s -> executor.isExist(Files1.getPath(request.getPath() + "/" + s)))
                .collect(Collectors.toList());
    }

    @Override
    public String getUploadAccessToken(String sessionToken) {
        Long[] tokenInfo = this.getTokenInfo(sessionToken);
        Long userId = Currents.getUserId();
        Valid.isTrue(tokenInfo[0].equals(userId), MessageConst.SESSION_EXPIRE);
        // 生成accessToken
        String accessToken = UUIds.random32();
        String key = Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, accessToken);
        redisTemplate.opsForValue().set(key, userId + "_" + tokenInfo[1], KeyConst.SFTP_UPLOAD_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
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
        Long machineId = valueInfo[1];
        Valid.notNull(this.getSessionStore(machineId), MessageConst.SESSION_EXPIRE);
        // 删除缓存
        redisTemplate.delete(key);
        return machineId;
    }

    @Override
    public void upload(Long machineId, List<FileUploadRequest> requestFiles) {
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 初始化上传信息
        List<FileTransferLogDO> uploadFiles = Lists.newList();
        for (FileUploadRequest requestFile : requestFiles) {
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
        }
        uploadFiles.forEach(fileTransferLogDAO::insert);
        // 提交上传进程
        String charset = this.getSftpCharset(machineId);
        for (FileTransferLogDO uploadFile : uploadFiles) {
            IFileTransferProcessor.of(uploadFile, charset).exec();
        }
        // 通知添加
        for (FileTransferLogDO uploadFile : uploadFiles) {
            FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
            notify.setType(SftpNotifyType.ADD.getType());
            notify.setFileToken(uploadFile.getFileToken());
            notify.setBody(Jsons.toJsonWriteNull(Converts.to(uploadFile, FileTransferLogVO.class)));
            transferProcessorManager.notifySession(userId, machineId, notify);
        }
    }

    @Override
    public void download(FileDownloadRequest request) {
        // 获取token信息
        Long machineId = this.getMachineId(request.getSessionToken());
        SftpExecutor executor = this.getBasicExecutor(machineId);
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 定义文件转换器
        Function<SftpFile, FileTransferLogDO> convert = file -> {
            // 本地保存路径
            String fileToken = ObjectIds.next();
            String localPath = PathBuilders.getSftpDownloadFilePath(fileToken);
            // 设置传输信息
            FileTransferLogDO download = new FileTransferLogDO();
            download.setUserId(userId);
            download.setUserName(user.getUsername());
            download.setFileToken(fileToken);
            download.setTransferType(SftpTransferType.DOWNLOAD.getType());
            download.setMachineId(machineId);
            download.setRemoteFile(file.getPath());
            download.setLocalFile(localPath);
            download.setCurrentSize(0L);
            download.setFileSize(file.getSize());
            download.setNowProgress(0D);
            download.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            return download;
        };
        // 初始化下载
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
        downloadFiles.forEach(fileTransferLogDAO::insert);
        // 提交上传进程
        String charset = this.getSftpCharset(machineId);
        for (FileTransferLogDO downloadFile : downloadFiles) {
            IFileTransferProcessor.of(downloadFile, charset).exec();
        }
        // 通知添加
        for (FileTransferLogDO downloadFile : downloadFiles) {
            FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
            notify.setType(SftpNotifyType.ADD.getType());
            notify.setFileToken(downloadFile.getFileToken());
            notify.setBody(Jsons.toJsonWriteNull(Converts.to(downloadFile, FileTransferLogVO.class)));
            transferProcessorManager.notifySession(userId, machineId, notify);
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
            // 修改状态为暂停
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(SftpTransferStatus.PAUSE.getStatus());
            fileTransferLogDAO.updateById(update);
            // 通知状态
            FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
            notify.setType(SftpNotifyType.CHANGE_STATUS.getType());
            notify.setFileToken(transferLog.getFileToken());
            notify.setBody(SftpTransferStatus.PAUSE.getStatus());
            transferProcessorManager.notifySession(transferLog.getUserId(), transferLog.getMachineId(), notify);
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
        // 修改状态为等待
        FileTransferLogDO update = new FileTransferLogDO();
        update.setId(transferLog.getId());
        update.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
        fileTransferLogDAO.updateById(update);
        // 通知状态
        FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
        notify.setType(SftpNotifyType.CHANGE_STATUS.getType());
        notify.setFileToken(transferLog.getFileToken());
        notify.setBody(SftpTransferStatus.WAIT.getStatus());
        transferProcessorManager.notifySession(transferLog.getUserId(), machineId, notify);
        // 提交下载
        String charset = this.getSftpCharset(machineId);
        IFileTransferProcessor.of(transferLog, charset).exec();
    }

    @Override
    public void transferPauseAll(String sessionToken) {
        // 获取token信息
        Long machineId = this.getMachineId(sessionToken);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .in(FileTransferLogDO::getTransferStatus, SftpTransferStatus.WAIT.getStatus(), SftpTransferStatus.RUNNABLE.getStatus());
        List<FileTransferLogDO> transferLogs = fileTransferLogDAO.selectList(wrapper);
        // 修改状态为暂停
        for (FileTransferLogDO transferLog : transferLogs) {
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(SftpTransferStatus.PAUSE.getStatus());
            fileTransferLogDAO.updateById(update);
        }
        // 通知状态
        for (FileTransferLogDO transferLog : transferLogs) {
            FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
            notify.setType(SftpNotifyType.CHANGE_STATUS.getType());
            notify.setFileToken(transferLog.getFileToken());
            notify.setBody(SftpTransferStatus.PAUSE.getStatus());
            transferProcessorManager.notifySession(transferLog.getUserId(), machineId, notify);
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
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.PAUSE.getStatus());
        List<FileTransferLogDO> transferLogs = fileTransferLogDAO.selectList(wrapper);
        // 修改状态为等待
        for (FileTransferLogDO transferLog : transferLogs) {
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(transferLog.getId());
            update.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            fileTransferLogDAO.updateById(update);
        }
        // 通知状态
        for (FileTransferLogDO transferLog : transferLogs) {
            FileTransferNotifyDTO notify = new FileTransferNotifyDTO();
            notify.setType(SftpNotifyType.CHANGE_STATUS.getType());
            notify.setFileToken(transferLog.getFileToken());
            notify.setBody(SftpTransferStatus.WAIT.getStatus());
            transferProcessorManager.notifySession(transferLog.getUserId(), machineId, notify);
        }
        // 提交下载
        String charset = this.getSftpCharset(machineId);
        for (FileTransferLogDO transferLog : transferLogs) {
            IFileTransferProcessor.of(transferLog, charset).exec();
        }
    }

    @Override
    public List<FileTransferLogVO> transferList(Long machineId) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .eq(FileTransferLogDO::getShowType, Const.ENABLE)
                .in(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType(), SftpTransferType.DOWNLOAD.getType())
                .orderByDesc(FileTransferLogDO::getId);
        List<FileTransferLogDO> logList = fileTransferLogDAO.selectList(wrapper);
        return Converts.toList(logList, FileTransferLogVO.class);
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
        transferLog.setLocalFile(MachineEnvAttr.SWAP_PATH.getValue() + transferLog.getLocalFile());
        return transferLog;
    }

    @Override
    public SftpExecutor getBasicExecutorByToken(String sessionToken) {
        Long[] values = this.getTokenInfo(sessionToken);
        boolean resolve = values[0].equals(Currents.getUserId());
        Valid.isTrue(resolve, MessageConst.SESSION_EXPIRE);
        // 检查缓存机器
        SftpExecutor executor = this.getBasicExecutor(values[1]);
        Valid.notNull(executor, MessageConst.SESSION_EXPIRE);
        if (!executor.isConnected()) {
            executor.connect();
        }
        return executor;
    }

    @Override
    public Long getMachineId(String sessionToken) {
        return this.getTokenInfo(sessionToken)[1];
    }

    @Override
    public Long[] getTokenInfo(String sessionToken) {
        Valid.notBlank(sessionToken, MessageConst.TOKEN_EMPTY);
        String key = Strings.format(KeyConst.SFTP_SESSION, sessionToken);
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
        String key = Strings.format(KeyConst.SFTP_SESSION, sessionToken);
        redisTemplate.opsForValue().set(key, userId + "_" + machineId, KeyConst.SFTP_SESSION_EXPIRE, TimeUnit.SECONDS);
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

    /**
     * 获取sftp编码格式
     *
     * @param machineId 机器id
     * @return 编码格式
     */
    private String getSftpCharset(Long machineId) {
        // 查询执行器
        SftpExecutor executor = basicExecutorHolder.get(machineId);
        if (executor != null) {
            return executor.getCharset();
        }
        // 查询环境变量
        return machineEnvService.getSftpCharset(machineId);
    }

    /**
     * 获取sftp基本操作executor
     *
     * @param machineId machineId
     * @return SftpExecutor
     */
    private SftpExecutor getBasicExecutor(Long machineId) {
        // 获取executor
        SftpExecutor executor = basicExecutorHolder.get(machineId);
        if (executor != null) {
            if (!executor.isConnected()) {
                try {
                    executor.connect();
                } catch (Exception e) {
                    // 无法连接则重新创建实例
                    executor = null;
                }
            }
        }
        if (executor == null) {
            // 获取charset
            String charset = this.getSftpCharset(machineId);
            // 打开sftp连接
            SessionStore sessionStore = this.getSessionStore(machineId);
            executor = sessionStore.getSftpExecutor(charset);
            executor.connect();
            basicExecutorHolder.put(machineId, executor);
        }
        return executor;
    }

    /**
     * 获取sessionStore
     *
     * @param machineId machineId
     * @return SessionStore
     */
    private SessionStore getSessionStore(Long machineId) {
        return machineInfoService.openSessionStore(machineId);
    }

}
