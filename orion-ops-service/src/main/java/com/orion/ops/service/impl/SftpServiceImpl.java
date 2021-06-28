package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
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
import com.orion.ops.handler.sftp.FileTransferProcessor;
import com.orion.ops.handler.sftp.TransferProcessorManager;
import com.orion.ops.handler.sftp.download.DownloadFileProcessor;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.remote.channel.sftp.SftpFile;
import com.orion.utils.Arrays1;
import com.orion.utils.Charsets;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
     * 连接信息
     */
    private final Map<Long, SessionStore> SESSION = new ConcurrentHashMap<>();

    /**
     * 基本操作的executor 不包含(upload, download)
     */
    private final Map<Long, SftpExecutor> BASIC_EXECUTOR = new ConcurrentHashMap<>();

    @Override
    public FileOpenVO open(Long machineId) {
        // 获取当前用户
        Long userId = Currents.getUserId();
        // 获取charset
        String charset = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.SFTP_CHARSET.name());
        if (!Charsets.isSupported(charset)) {
            charset = Const.UTF_8;
        }
        // 获取executor
        SftpExecutor executor = BASIC_EXECUTOR.get(machineId);
        if (executor == null || !executor.isConnected()) {
            // 打开sftp连接
            SessionStore sessionStore = machineInfoService.openSessionStore(machineId);
            executor = sessionStore.getSftpExecutor(charset);
            executor.connect();
            SESSION.put(machineId, sessionStore);
            BASIC_EXECUTOR.put(machineId, executor);
        }
        // 生成token
        String token = this.generatorToken(userId, machineId);
        // 查询列表
        String path = executor.getHome();
        FileListVO list = this.list(path, 0, executor);
        // 返回数据
        FileOpenVO openVO = new FileOpenVO();
        openVO.setToken(token);
        openVO.setHome(path);
        openVO.setCharset(charset);
        openVO.setPath(list.getPath());
        openVO.setFiles(list.getFiles());
        return openVO;
    }

    @Override
    public FileListVO list(FileListRequest request) {
        return this.list(request.getPath(), request.getAll(), request.getExecutor());
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
        List<FileDetailVO> files = fileList.stream()
                .map(f -> Converts.to(f, FileDetailVO.class))
                .collect(Collectors.toList());
        fileListVO.setPath(path);
        fileListVO.setFiles(files);
        return fileListVO;
    }

    @Override
    public void mkdir(FileMkdirRequest request) {
        String path = Files1.getPath(request.getCurrent() + "/" + request.getPath());
        boolean r = request.getExecutor().mkdirs(path);
        Valid.sftp(r);
    }

    @Override
    public void touch(FileTouchRequest request) {
        String path = Files1.getPath(request.getCurrent() + "/" + request.getPath());
        boolean r = request.getExecutor().touch(path);
        Valid.sftp(r);
    }

    @Override
    public void truncate(FileTruncateRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().truncate(path);
        Valid.sftp(r);
    }

    @Override
    public void move(FileMoveRequest request) {
        String source = Files1.getPath(request.getSource());
        String target = Files1.getPath(request.getTarget());
        boolean r = request.getExecutor().mv(source, target);
        Valid.sftp(r);
    }

    @Override
    public void remove(FileRemoveRequest request) {
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = request.getExecutor();
        SftpFile file = executor.getFile(path);
        Valid.notNull(file, MessageConst.FILE_NOTFOUND);
        boolean r = executor.rm(path);
        Valid.sftp(r);
    }

    @Override
    public void chmod(FileChmodRequest request) {
        String path = Files1.getPath(request.getPath());
        boolean r = request.getExecutor().chmod(path, request.getPermission());
        Valid.sftp(r);
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
    public String download(FileDownloadRequest request) {
        // 查询远程文件是否存在
        String path = Files1.getPath(request.getPath());
        SftpExecutor executor = request.getExecutor();
        SftpFile file = executor.getFile(path);
        Valid.notNull(file, MessageConst.FILE_NOTFOUND);
        // 获取token信息
        Long machineId = this.getTokenInfo(request.getToken())[1];
        String token = ObjectIds.next();
        UserDTO user = Currents.getUser();
        // 获取连接
        SessionStore session = SESSION.get(machineId);
        Valid.notNull(session, MessageConst.SESSION_EXPIRE);
        // 构建下载参数
        FileTransferHint hint = new FileTransferHint();
        hint.setToken(token);
        hint.setUserId(user.getId());
        hint.setUsername(user.getUsername());
        hint.setMachineId(machineId);
        hint.setRemoteFile(path);
        hint.setLocalFile(Const.DOWNLOAD_DIR + "/" + token + ".swp");
        hint.setFileSize(file.getSize());
        hint.setCharset(executor.getCharset());
        hint.setTransferType(SftpTransferType.DOWNLOAD);
        // 提交下载
        new DownloadFileProcessor(hint, session).exec();
        return token;
    }

    @Override
    public void downloadResume(String token) {
        FileTransferLogDO transferLog = this.getTransferLogByToken(token);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        FileTransferProcessor processor = transferProcessorManager.getProcessor(token);
        if (processor != null) {
            return;
        }
        // 查询远程文件是否存在
        SftpExecutor executor = BASIC_EXECUTOR.get(transferLog.getMachineId());
        Valid.notNull(executor, MessageConst.SESSION_EXPIRE);
        if (executor.isClosed()) {
            executor.connect();
        }
        SftpFile file = executor.getFile(transferLog.getRemoteFile());
        Valid.notNull(file, MessageConst.FILE_NOTFOUND);
        // 获取连接
        SessionStore session = SESSION.get(transferLog.getMachineId());
        Valid.notNull(session, MessageConst.SESSION_EXPIRE);
        // 构建下载参数
        FileTransferHint hint = new FileTransferHint();
        hint.setResumeId(transferLog.getId());
        hint.setToken(token);
        hint.setUserId(transferLog.getUserId());
        hint.setUsername(transferLog.getUserName());
        hint.setMachineId(transferLog.getMachineId());
        hint.setRemoteFile(transferLog.getRemoteFile());
        hint.setLocalFile(transferLog.getLocalFile());
        hint.setFileSize(transferLog.getFileSize());
        hint.setCharset(executor.getCharset());
        hint.setTransferType(SftpTransferType.DOWNLOAD);
        // 提交下载
        new DownloadFileProcessor(hint, session).resume();
    }

    @Override
    public List<FileTransferLogVO> transferList(Long machineId) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getUserId, Currents.getUserId())
                .eq(FileTransferLogDO::getMachineId, machineId)
                .in(FileTransferLogDO::getTransferType, SftpTransferType.UPLOAD.getType(), SftpTransferType.DOWNLOAD.getType())
                .orderByDesc(FileTransferLogDO::getId);
        return fileTransferLogDAO.selectList(wrapper).stream()
                .map(s -> Converts.to(s, FileTransferLogVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void transferStop(String token) {
        FileTransferProcessor transferProcessor = this.getTransferProcessor(token);
        transferProcessor.stop();
    }

    @Override
    public SftpExecutor getBasicExecutorByToken(String token) {
        Long[] values = this.getTokenInfo(token);
        boolean resolve = values[0].equals(Currents.getUserId());
        Valid.isTrue(resolve, MessageConst.SESSION_EXPIRE);
        // 检查缓存机器
        SftpExecutor executor = BASIC_EXECUTOR.get(values[1]);
        Valid.notNull(executor, MessageConst.SESSION_EXPIRE);
        if (executor.isClosed()) {
            executor.connect();
        }
        return executor;
    }

    @Override
    public Long[] getTokenInfo(String token) {
        Valid.notBlank(token, MessageConst.TOKEN_EMPTY);
        String key = Strings.format(KeyConst.SFTP_SESSION, token);
        String value = redisTemplate.opsForValue().get(key);
        Valid.notBlank(value, MessageConst.SESSION_EXPIRE);
        return Arrays1.mapper(Objects.requireNonNull(value).split("_"), Long[]::new, Long::valueOf);
    }

    /**
     * 生成token
     *
     * @param userId    userId
     * @param machineId 机器id
     * @return token
     */
    private String generatorToken(Long userId, Long machineId) {
        // 生成token
        String token = UUIds.random15();
        // 设置缓存
        String key = Strings.format(KeyConst.SFTP_SESSION, token);
        redisTemplate.opsForValue().set(key, userId + "_" + machineId, KeyConst.SFTP_SESSION_EXPIRE, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 查询传输日志
     *
     * @param token token
     * @return FileTransferLogDO
     */
    private FileTransferLogDO getTransferLogByToken(String token) {
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getToken, token)
                .eq(FileTransferLogDO::getUserId, Currents.getUserId());
        return fileTransferLogDAO.selectOne(wrapper);
    }

    /**
     * 获取传输对象处理器
     *
     * @param token token
     * @return FileTransferProcessor
     */
    private FileTransferProcessor getTransferProcessor(String token) {
        FileTransferLogDO transferLog = this.getTransferLogByToken(token);
        Valid.notNull(transferLog, MessageConst.UNSELECTED_TRANSFER_LOG);
        FileTransferProcessor processor = transferProcessorManager.getProcessor(token);
        return Valid.notNull(processor, MessageConst.UNSELECTED_TRANSFER_PROCESSOR);
    }

}
