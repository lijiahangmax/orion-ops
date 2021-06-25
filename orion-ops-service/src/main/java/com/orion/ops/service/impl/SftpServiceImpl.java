package com.orion.ops.service.impl;

import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.sftp.FileDetailVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.remote.channel.sftp.SftpFile;
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
    private RedisTemplate<String, String> redisTemplate;

    private final Map<String, SftpExecutor> SESSION = new ConcurrentHashMap<>();

    @Override
    public FileOpenVO open(Long machineId) {
        // 获取当前用户
        Long userId = Currents.getUserId();
        String key = userId + "_" + machineId;
        // 获取charset
        String charset = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.SFTP_CHARSET.name());
        if (!Charsets.isSupported(charset)) {
            charset = Const.UTF_8;
        }
        // 获取executor
        SftpExecutor executor = SESSION.get(key);
        if (executor == null || !executor.isConnected()) {
            // 打开sftp连接
            SessionStore sessionStore = machineInfoService.openSessionStore(machineId);
            executor = sessionStore.getSftpExecutor(charset);
            executor.connect();
            SESSION.put(key, executor);
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
        Valid.sftp(file != null);
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
     * 通过token获取executor
     *
     * @param token token
     * @return SftpExecutor
     */
    @Override
    public SftpExecutor getExecutorByToken(String token) {
        Valid.notBlank(token, MessageConst.TOKEN_EMPTY);
        String key = Strings.format(KeyConst.SFTP_SESSION, token);
        String value = redisTemplate.opsForValue().get(key);
        Valid.notBlank(value, MessageConst.SESSION_EXPIRE);
        boolean resolve = Objects.requireNonNull(value).split("_")[0].equals(Currents.getUserId() + "");
        Valid.isTrue(resolve, MessageConst.SESSION_EXPIRE);
        SftpExecutor executor = SESSION.get(value);
        Valid.notNull(executor, MessageConst.SESSION_EXPIRE);
        return executor;
    }

}
