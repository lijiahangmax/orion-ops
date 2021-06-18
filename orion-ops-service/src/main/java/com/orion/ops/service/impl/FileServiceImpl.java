package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.dao.MachineTerminalLogDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.FileDownloadDTO;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.service.api.CommandExecService;
import com.orion.ops.service.api.FileService;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Currents;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:37
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Resource
    private MachineTerminalLogDAO machineTerminalLogDAO;

    @Resource
    private CommandExecService commandExecService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HttpWrapper<String> getDownloadToken(Long id, FileDownloadType type) {
        String path;
        // 获取日志绝对路径
        switch (type) {
            case SECRET_KEY:
                path = this.getDownloadSecretKeyFilePath(id);
                break;
            case TERMINAL_LOG:
                path = this.getDownloadTerminalLogFilePath(id);
                break;
            case EXEC_LOG:
                path = commandExecService.getExecLogFilePath(id);
                break;
            default:
                path = null;
                break;
        }
        // 检查文件是否存在
        if (path == null || !Files1.isFile(path)) {
            return HttpWrapper.of(ResultCode.FILE_MISSING);
        }
        // 设置缓存
        FileDownloadDTO download = new FileDownloadDTO();
        download.setFilePath(path);
        download.setUserId(Currents.getUserId());
        String token = UUIds.random19();
        String key = Strings.format(KeyConst.FILE_DOWNLOAD, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(download), KeyConst.FILE_DOWNLOAD_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<String>ok().data(token);
    }

    @Override
    public String getPathByDownloadToken(String token) {
        if (Strings.isBlank(token)) {
            return null;
        }
        String key = Strings.format(KeyConst.FILE_DOWNLOAD, token);
        String value = redisTemplate.opsForValue().get(key);
        if (Strings.isBlank(value)) {
            return null;
        }
        FileDownloadDTO download = JSON.parseObject(value, FileDownloadDTO.class);
        if (download == null) {
            return null;
        }
        if (!Currents.getUserId().equals(download.getUserId())) {
            return null;
        }
        redisTemplate.delete(key);
        return download.getFilePath();
    }

    @Override
    public HttpWrapper<String> getTailToken(FileTailRequest request) {
        Long relId = request.getRelId();
        String path;
        Long machineId;
        // 获取日志路径
        switch (FileTailType.of(request.getType())) {
            case EXEC_LOG:
                path = commandExecService.getExecLogFilePath(relId);
                machineId = Const.HOST_MACHINE_ID;
                break;
            default:
                path = null;
                machineId = Const.HOST_MACHINE_ID;
                break;
        }
        // 检查文件是否存在
        if (path == null || !Files1.isFile(path)) {
            return HttpWrapper.of(ResultCode.FILE_MISSING);
        }
        // 设置缓存
        FileTailDTO tail = new FileTailDTO();
        tail.setFilePath(path);
        tail.setUserId(Currents.getUserId());
        tail.setMachineId(machineId);
        String token = UUIds.random19();
        String key = Strings.format(KeyConst.FILE_TAIL_ACCESS, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(tail), KeyConst.FILE_TAIL_ACCESS_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<String>ok().data(token);
    }

    /**
     * 获取下载 秘钥路径
     *
     * @param id id
     * @return path
     * @see FileDownloadType#SECRET_KEY
     */
    private String getDownloadSecretKeyFilePath(Long id) {
        return Optional.ofNullable(machineSecretKeyDAO.selectById(id))
                .map(MachineSecretKeyDO::getSecretKeyPath)
                .map(MachineKeyService::getKeyPath)
                .filter(Strings::isNotBlank)
                .orElse(null);
    }

    /**
     * 获取下载 terminal日志路径
     *
     * @param id id
     * @return path
     * @see FileDownloadType#TERMINAL_LOG
     */
    private String getDownloadTerminalLogFilePath(Long id) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .like(!Currents.isAdministrator(), MachineTerminalLogDO::getUserId, Currents.getUserId())
                .eq(MachineTerminalLogDO::getId, id);
        return Optional.ofNullable(machineTerminalLogDAO.selectOne(wrapper))
                .map(MachineTerminalLogDO::getOperateLogFile)
                .filter(Strings::isNotBlank)
                .map(s -> MachineEnvAttr.LOG_PATH.getValue() + s)
                .orElse(null);
    }

}
