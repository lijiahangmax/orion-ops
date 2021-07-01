package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.dao.MachineTerminalLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.FileDownloadDTO;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailVO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.utils.Charsets;
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
    private MachineEnvService machineEnvService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private CommandExecService commandExecService;

    @Resource
    private SftpService sftpService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HttpWrapper<String> getDownloadToken(Long id, FileDownloadType type) {
        String path = null;
        String name = null;
        // 获取日志绝对路径
        switch (type) {
            case SECRET_KEY:
                path = this.getDownloadSecretKeyFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case TERMINAL_LOG:
                path = this.getDownloadTerminalLogFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case EXEC_LOG:
                path = commandExecService.getExecLogFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case SFTP_DOWNLOAD:
                FileTransferLogDO transferLog = sftpService.getDownloadFilePath(id);
                if (transferLog != null) {
                    path = transferLog.getLocalFile();
                    name = Files1.getFileName(transferLog.getRemoteFile());
                }
                break;
            default:
                break;
        }
        // 检查文件是否存在
        if (path == null || !Files1.isFile(path)) {
            return HttpWrapper.of(ResultCode.FILE_MISSING);
        }
        // 设置缓存
        FileDownloadDTO download = new FileDownloadDTO();
        download.setFilePath(path);
        download.setFileName(Strings.def(name, Const.UNKNOWN));
        download.setUserId(Currents.getUserId());
        String token = UUIds.random19();
        String key = Strings.format(KeyConst.FILE_DOWNLOAD, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(download), KeyConst.FILE_DOWNLOAD_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<String>ok().data(token);
    }

    @Override
    public FileDownloadDTO getPathByDownloadToken(String token) {
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
        redisTemplate.delete(key);
        return download;
    }

    @Override
    public HttpWrapper<FileTailVO> getTailToken(FileTailRequest request) {
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
                machineId = -1L;
                break;
        }
        // 检查文件是否存在
        if (Const.HOST_MACHINE_ID.equals(machineId)) {
            if (path == null || !Files1.isFile(path)) {
                return HttpWrapper.of(ResultCode.FILE_MISSING);
            }
        }
        // 查询机器
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        if (machine == null) {
            return HttpWrapper.error(MessageConst.INVALID_MACHINE);
        }
        // 设置缓存
        FileTailDTO tail = new FileTailDTO();
        tail.setFilePath(path);
        tail.setUserId(Currents.getUserId());
        tail.setMachineId(machineId);
        tail.setMode(this.getMachineTailMode(machineId));
        Integer offset = request.getOffset();
        if (offset != null) {
            tail.setOffset(offset);
        } else {
            tail.setOffset(this.getTailOffset(machineId));
        }
        String charset = request.getCharset();
        if (charset != null) {
            tail.setCharset(charset);
        } else {
            tail.setCharset(this.getCharset(machineId));
        }
        String token = UUIds.random19();
        String key = Strings.format(KeyConst.FILE_TAIL_ACCESS, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(tail), KeyConst.FILE_TAIL_ACCESS_EXPIRE, TimeUnit.SECONDS);
        // 设置返回
        FileTailVO res = new FileTailVO();
        res.setToken(token);
        res.setHost(machine.getMachineHost());
        res.setFile(tail.getFilePath());
        res.setOffset(tail.getOffset());
        res.setCharset(tail.getCharset());
        return HttpWrapper.<FileTailVO>ok().data(res);
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
                .eq(!Currents.isAdministrator(), MachineTerminalLogDO::getUserId, Currents.getUserId())
                .eq(MachineTerminalLogDO::getId, id);
        return Optional.ofNullable(machineTerminalLogDAO.selectOne(wrapper))
                .map(MachineTerminalLogDO::getOperateLogFile)
                .filter(Strings::isNotBlank)
                .map(s -> MachineEnvAttr.LOG_PATH.getValue() + s)
                .orElse(null);
    }

    /**
     * 获取文件tail模型
     *
     * @param machineId 机器id
     * @return mode
     */
    private String getMachineTailMode(Long machineId) {
        if (Const.HOST_MACHINE_ID.equals(machineId)) {
            String mode = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.TAIL_MODE.name());
            return FileTailMode.of(mode, true).getMode();
        } else {
            return FileTailMode.TAIL.getMode();
        }
    }

    /**
     * 获取文件tail 尾行偏移量
     *
     * @param machineId 机器id
     * @return offset line
     */
    private Integer getTailOffset(Long machineId) {
        String offset = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.TAIL_OFFSET.name());
        if (Strings.isInteger(offset)) {
            return Integer.valueOf(offset);
        } else {
            return Const.TAIL_OFFSET_LINE;
        }
    }

    /**
     * 获取编码集
     *
     * @param machineId 机器id
     * @return 编码集
     */
    private String getCharset(Long machineId) {
        String charset = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.TAIL_CHARSET.name());
        if (Charsets.isSupported(charset)) {
            return charset;
        } else {
            return Const.UTF_8;
        }
    }

}
