package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.DownloadType;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.dao.MachineTerminalLogDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.FileDownloadDTO;
import com.orion.ops.service.api.FileDownloadService;
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
@Service("fileDownloadService")
public class FileDownloadServiceImpl implements FileDownloadService {

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Resource
    private MachineTerminalLogDAO machineTerminalLogDAO;

    @Resource
    private CommandExecDAO commandExecDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HttpWrapper<String> checkFile(Long id, DownloadType type) {
        String path;
        // 获取日志绝对路径
        switch (type) {
            case SECRET_KEY:
                path = this.getSecretKeyFilePath(id);
                break;
            case TERMINAL_LOG:
                path = this.getTerminalLogFilePath(id);
                break;
            case EXEC_LOG:
                path = this.getExecLogFilePath(id);
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
        String token = UUIds.random15();
        String key = Strings.format(KeyConst.FILE_DOWNLOAD, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(download), KeyConst.FILE_DOWNLOAD_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<String>ok().data(token);
    }

    @Override
    public String getPathByToken(String token) {
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

    /**
     * 获取秘钥路径
     *
     * @param id id
     * @return path
     * @see DownloadType#SECRET_KEY
     */
    private String getSecretKeyFilePath(Long id) {
        return Optional.ofNullable(machineSecretKeyDAO.selectById(id))
                .map(MachineSecretKeyDO::getSecretKeyPath)
                .map(MachineKeyService::getKeyPath)
                .orElse(null);
    }

    /**
     * 获取 terminal 日志
     *
     * @param id id
     * @return path
     * @see DownloadType#TERMINAL_LOG
     */
    private String getTerminalLogFilePath(Long id) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .like(!Currents.isAdministrator(), MachineTerminalLogDO::getUserId, Currents.getUserId())
                .eq(MachineTerminalLogDO::getId, id);
        return Optional.ofNullable(machineTerminalLogDAO.selectOne(wrapper))
                .map(MachineTerminalLogDO::getOperateLogFile)
                .map(s -> MachineEnvAttr.LOG_PATH.getValue() + s)
                .orElse(null);
    }

    /**
     * 获取 exec command 日志
     *
     * @param id id
     * @return logPath
     * @see DownloadType#EXEC_LOG
     */
    private String getExecLogFilePath(Long id) {
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .like(!Currents.isAdministrator(), CommandExecDO::getUserId, Currents.getUserId())
                .eq(CommandExecDO::getId, id);
        return Optional.ofNullable(commandExecDAO.selectOne(wrapper))
                .map(CommandExecDO::getLogPath)
                .map(s -> MachineEnvAttr.LOG_PATH.getValue() + s)
                .orElse(null);
    }

}
