package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.consts.sftp.SftpTransferType;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileDetailVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.handler.sftp.FileTransferHint;
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
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
        SftpExecutor executor = getBasicExecutor(machineId);
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
        return this.list(request.getPath(), request.getAll(), request.getExecutor());
    }

    @Override
    public FileListVO listDir(FileListRequest request) {
        List<SftpFile> fileList;
        SftpExecutor executor = request.getExecutor();
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
    public FileListVO list(String path, int all, SftpExecutor executor) {
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
    public String mkdir(FileMkdirRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().mkdirs(path);
        Valid.sftp(r);
        return path;
    }

    @Override
    public String touch(FileTouchRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().touch(path);
        Valid.sftp(r);
        return path;
    }

    @Override
    public void truncate(FileTruncateRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().truncate(path);
        Valid.sftp(r);
    }

    @Override
    public String move(FileMoveRequest request) {
        String source = Files1.getPath(request.getSource());
        String target = Files1.getPath(request.getTarget());
        boolean r = request.getExecutor().mv(source, target);
        Valid.sftp(r);
        return target;
    }

    @Override
    public void remove(FileRemoveRequest request) {
        SftpExecutor executor = request.getExecutor();
        request.getPaths().stream()
                .map(Files1::getPath)
                .forEach(executor::rm);
    }

    @Override
    public String chmod(FileChmodRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().chmod(path, request.getPermission());
        Valid.sftp(r);
        return Files1.permission10toString(request.getPermission());
    }

    @Override
    public void chown(FileChownRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().chown(path, request.getUid());
        Valid.sftp(r);
    }

    @Override
    public void changeGroup(FileChangeGroupRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().chgrp(path, request.getGid());
        Valid.sftp(r);
    }

    @Override
    public List<String> checkFilePresent(FilePresentCheckRequest request) {
        SftpExecutor executor = request.getExecutor();
        return request.getNames().stream()
                .filter(Strings::isNotBlank)
                .filter(s -> executor.isExist(Files1.getPath(request.getPath() + "/" + s)))
                .collect(Collectors.toList());
    }

    @Override
    public String getUploadToken(String sessionToken) {
        Long[] tokenInfo = this.getTokenInfo(sessionToken);
        Long userId = Currents.getUserId();
        Valid.isTrue(tokenInfo[0].equals(userId), MessageConst.SESSION_EXPIRE);
        // 生成文件token
        String fileToken = ObjectIds.next();
        String key = Strings.format(KeyConst.SFTP_UPLOAD_TOKEN, fileToken);
        redisTemplate.opsForValue().set(key, userId + "_" + tokenInfo[1], KeyConst.SFTP_UPLOAD_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return fileToken;
    }

    @Override
    public Long checkUploadToken(String fileToken) {
        // 获取缓存
        String key = Strings.format(KeyConst.SFTP_UPLOAD_TOKEN, fileToken);
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
    public String upload(FileUploadRequest request) {
        // 获取连接
        Long machineId = request.getMachineId();
        SessionStore session = this.getSessionStore(machineId);
        Valid.notNull(session, MessageConst.SESSION_EXPIRE);
        UserDTO user = Currents.getUser();
        // 构建下载参数
        FileTransferHint hint = new FileTransferHint();
        hint.setFileToken(request.getFileToken());
        hint.setUserId(user.getId());
        hint.setUsername(user.getUsername());
        hint.setMachineId(machineId);
        hint.setRemoteFile(request.getRemotePath());
        hint.setLocalFile(request.getLocalPath());
        hint.setFileSize(request.getSize());
        hint.setCharset(this.getSftpCharset(machineId));
        hint.setTransferType(SftpTransferType.UPLOAD);
        // 提交上传
        IFileTransferProcessor.of(hint, session).exec();
        return request.getFileToken();
    }

    @Override
    public String download(FileDownloadRequest request) {
        // 查询远程文件是否存在
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = request.getExecutor();
        SftpFile file = executor.getFile(path);
        Valid.notNull(file, MessageConst.FILE_NOT_FOUND);
        // 获取token信息
        Long machineId = this.getTokenInfo(request.getSessionToken())[1];
        String fileToken = ObjectIds.next();
        UserDTO user = Currents.getUser();
        // 获取连接
        SessionStore session = this.getSessionStore(machineId);
        Valid.notNull(session, MessageConst.SESSION_EXPIRE);
        // 构建下载参数
        FileTransferHint hint = new FileTransferHint();
        hint.setFileToken(fileToken);
        hint.setUserId(user.getId());
        hint.setUsername(user.getUsername());
        hint.setMachineId(machineId);
        hint.setRemoteFile(path);
        hint.setLocalFile(PathBuilders.getSftpDownloadFilePath(fileToken));
        hint.setFileSize(file.getSize());
        hint.setCharset(executor.getCharset());
        hint.setTransferType(SftpTransferType.DOWNLOAD);
        // 提交下载
        IFileTransferProcessor.of(hint, session).exec();
        return fileToken;
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
    public void transferStop(String fileToken) {
        IFileTransferProcessor transferProcessor = this.getTransferProcessor(fileToken);
        transferProcessor.stop();
    }

    @Override
    public void transferResume(String fileToken) {
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        Valid.isTrue(SftpTransferStatus.PAUSE.getStatus().equals(transferLog.getTransferStatus()), MessageConst.INVALID_STATUS);
        IFileTransferProcessor processor = transferProcessorManager.getProcessor(fileToken);
        if (processor != null) {
            return;
        }
        // 获取连接
        SessionStore session = this.getSessionStore(transferLog.getMachineId());
        Valid.notNull(session, MessageConst.SESSION_EXPIRE);
        // 构建下载参数
        FileTransferHint hint = new FileTransferHint();
        hint.setResumeId(transferLog.getId());
        hint.setFileToken(fileToken);
        hint.setUserId(transferLog.getUserId());
        hint.setUsername(transferLog.getUserName());
        hint.setMachineId(transferLog.getMachineId());
        hint.setRemoteFile(transferLog.getRemoteFile());
        hint.setLocalFile(transferLog.getLocalFile());
        hint.setFileSize(transferLog.getFileSize());
        hint.setCharset(this.getSftpCharset(transferLog.getMachineId()));
        hint.setTransferType(SftpTransferType.of(transferLog.getTransferType()));
        // 提交下载
        IFileTransferProcessor.of(hint, session).resume();
    }

    @Override
    public Integer transferRemove(String fileToken) {
        FileTransferLogDO update = new FileTransferLogDO();
        update.setShowType(Const.DISABLE);
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getFileToken, fileToken)
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .ne(FileTransferLogDO::getTransferStatus, SftpTransferStatus.RUNNABLE.getStatus());
        return fileTransferLogDAO.update(update, wrapper);
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
     * 获取传输对象处理器
     *
     * @param fileToken fileToken
     * @return FileTransferProcessor
     */
    private IFileTransferProcessor getTransferProcessor(String fileToken) {
        FileTransferLogDO transferLog = this.getTransferLogByToken(fileToken);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        IFileTransferProcessor processor = transferProcessorManager.getProcessor(fileToken);
        return Valid.notNull(processor, MessageConst.UNSELECTED_TRANSFER_PROCESSOR);
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
