package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.dao.MachineTerminalLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.entity.dto.FileDownloadDTO;
import com.orion.ops.service.api.*;
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
    private CommandExecService commandExecService;

    @Resource
    private ReleaseInfoService releaseInfoService;

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
                // 秘钥
                path = this.getDownloadSecretKeyFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case TERMINAL_LOG:
                // terminal 日志
                path = this.getDownloadTerminalLogFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case EXEC_LOG:
                // 执行日志
                path = commandExecService.getExecLogFilePath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case SFTP_DOWNLOAD:
                // sftp 下载文件
                FileTransferLogDO transferLog = sftpService.getDownloadFilePath(id);
                if (transferLog != null) {
                    path = transferLog.getLocalFile();
                    name = Files1.getFileName(transferLog.getRemoteFile());
                }
                break;
            case RELEASE_HOST_LOG:
                // 上线单宿主机日志
                path = releaseInfoService.getReleaseHostLogPath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case RELEASE_STAGE_LOG:
                // 上线单目标机器日志
                path = releaseInfoService.getReleaseStageLogPath(id);
                name = Optional.ofNullable(path).map(Files1::getFileName).orElse(null);
                break;
            case RELEASE_SNAPSHOT:
                // 上线单产物快照
                ReleaseBillDO releaseBill = releaseInfoService.getReleaseBill(id);
                if (releaseBill != null) {
                    path = releaseInfoService.getDistSnapshotFilePath(releaseBill.getDistSnapshotPath());
                    name = Optional.ofNullable(releaseBill.getDistPath()).map(Files1::getFileName)
                            .map(n -> releaseBill.getId() + "_" + n).orElse(null);
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
        String key = Strings.format(KeyConst.FILE_DOWNLOAD_TOKEN, token);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(download), KeyConst.FILE_DOWNLOAD_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.<String>ok().data(token);
    }

    @Override
    public FileDownloadDTO getPathByDownloadToken(String token) {
        if (Strings.isBlank(token)) {
            return null;
        }
        String key = Strings.format(KeyConst.FILE_DOWNLOAD_TOKEN, token);
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
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

}
