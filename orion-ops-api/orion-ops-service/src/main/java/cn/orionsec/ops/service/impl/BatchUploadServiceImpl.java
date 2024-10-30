/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.sftp.SftpTransferStatus;
import cn.orionsec.ops.constant.sftp.SftpTransferType;
import cn.orionsec.ops.dao.FileTransferLogDAO;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.dto.sftp.SftpSessionTokenDTO;
import cn.orionsec.ops.entity.dto.sftp.SftpUploadInfoDTO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.entity.request.sftp.FileUploadRequest;
import cn.orionsec.ops.entity.request.upload.BatchUploadRequest;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckFileVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckMachineVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadTokenVO;
import cn.orionsec.ops.handler.sftp.IFileTransferProcessor;
import cn.orionsec.ops.handler.sftp.SftpBasicExecutorHolder;
import cn.orionsec.ops.handler.sftp.TransferProcessorManager;
import cn.orionsec.ops.handler.sftp.hint.FileTransferHint;
import cn.orionsec.ops.service.api.BatchUploadService;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.alibaba.fastjson.JSON;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 批量上传
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 15:48
 */
@Service("batchUploadService")
public class BatchUploadServiceImpl implements BatchUploadService {

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private SftpBasicExecutorHolder sftpBasicExecutorHolder;

    @Resource
    private TransferProcessorManager transferProcessorManager;

    @Resource
    private FileTransferLogDAO fileTransferLogDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public BatchUploadCheckVO checkMachineFiles(BatchUploadRequest request) {
        // 机器信息
        Map<Long, MachineInfoDO> machineMap = Maps.newMap();
        // sftp 连接
        Map<Long, SftpExecutor> connectedExecutors = Maps.newLinkedMap();
        // 不可连接的机器
        List<Long> notConnectMachineId = Lists.newList();
        for (Long machineId : request.getMachineIds()) {
            // 查询机器
            MachineInfoDO machine = machineInfoService.selectById(machineId);
            machineMap.put(machineId, machine);
            // 获取连接
            try {
                SftpExecutor executor = sftpBasicExecutorHolder.getBasicExecutor(machineId, machine);
                connectedExecutors.put(machineId, executor);
            } catch (Exception e) {
                notConnectMachineId.add(machineId);
            }
        }

        // 检查可连接机器文件是否存在
        List<BatchUploadCheckFileVO> machinePresentFiles = Lists.newList();
        connectedExecutors.forEach((k, v) -> {
            synchronized (v) {
                List<String> presentFiles = request.getNames().stream()
                        .filter(Strings::isNotBlank)
                        .filter(s -> v.getFile(Files1.getPath(request.getRemotePath(), s)) != null)
                        .collect(Collectors.toList());
                if (!presentFiles.isEmpty()) {
                    // 记录重复文件
                    BatchUploadCheckFileVO checkFile = Converts.to(machineMap.get(k), BatchUploadCheckFileVO.class);
                    checkFile.setPresentFiles(presentFiles);
                    machinePresentFiles.add(checkFile);
                }
            }
        });

        // 返回
        BatchUploadCheckVO checkResult = new BatchUploadCheckVO();
        checkResult.setMachinePresentFiles(machinePresentFiles);
        // 可连接的机器
        Set<Long> connectedMachineIdList = connectedExecutors.keySet();
        checkResult.setConnectedMachineIdList(connectedMachineIdList);
        List<BatchUploadCheckMachineVO> connectedMachines = connectedMachineIdList.stream()
                .map(machineMap::get)
                .filter(Objects::nonNull)
                .map(s -> Converts.to(s, BatchUploadCheckMachineVO.class))
                .collect(Collectors.toList());
        checkResult.setConnectedMachines(connectedMachines);
        // 无法连接的机器
        List<BatchUploadCheckMachineVO> notConnectMachines = notConnectMachineId.stream()
                .map(machineMap::get)
                .filter(Objects::nonNull)
                .map(s -> Converts.to(s, BatchUploadCheckMachineVO.class))
                .collect(Collectors.toList());
        checkResult.setNotConnectedMachines(notConnectMachines);
        return checkResult;
    }

    @Override
    public BatchUploadTokenVO getUploadAccessToken(BatchUploadRequest request) {
        Long userId = Currents.getUserId();
        // 生成 accessToken
        String accessToken = UUIds.random32();
        String accessKey = Strings.format(KeyConst.SFTP_UPLOAD_ACCESS_TOKEN, accessToken);
        // 设置缓存信息
        SftpUploadInfoDTO uploadInfo = Converts.to(request, SftpUploadInfoDTO.class);
        uploadInfo.setUserId(userId);
        redisTemplate.opsForValue().set(accessKey, JSON.toJSONString(uploadInfo),
                KeyConst.SFTP_UPLOAD_ACCESS_EXPIRE, TimeUnit.SECONDS);

        // 生成 notifyToken
        String notifyToken = UUIds.random15();
        // 设置缓存信息
        String notifyKey = Strings.format(KeyConst.SFTP_SESSION_TOKEN, notifyToken);
        SftpSessionTokenDTO info = new SftpSessionTokenDTO();
        info.setUserId(userId);
        info.setMachineIdList(uploadInfo.getMachineIdList());
        redisTemplate.opsForValue().set(notifyKey, JSON.toJSONString(info), KeyConst.SFTP_SESSION_EXPIRE, TimeUnit.SECONDS);

        // 返回
        BatchUploadTokenVO token = new BatchUploadTokenVO();
        token.setAccessToken(accessToken);
        token.setNotifyToken(notifyToken);
        return token;
    }

    @Override
    public List<String> batchUpload(List<FileUploadRequest> requestFiles) {
        UserDTO user = Currents.getUser();
        Long userId = user.getId();
        // 初始化上传信息
        List<FileTransferLogDO> uploadFiles = Lists.newList();
        for (FileUploadRequest requestFile : requestFiles) {
            // 插入明细
            FileTransferLogDO upload = new FileTransferLogDO();
            upload.setUserId(userId);
            upload.setUserName(user.getUsername());
            upload.setMachineId(requestFile.getMachineId());
            upload.setFileToken(requestFile.getFileToken());
            upload.setTransferType(SftpTransferType.UPLOAD.getType());
            upload.setRemoteFile(requestFile.getRemotePath());
            upload.setLocalFile(requestFile.getLocalPath());
            upload.setNowProgress(0D);
            upload.setCurrentSize(0L);
            upload.setFileSize(requestFile.getSize());
            upload.setTransferStatus(SftpTransferStatus.WAIT.getStatus());
            uploadFiles.add(upload);
            fileTransferLogDAO.insert(upload);
            // 通知添加
            transferProcessorManager.notifySessionAddEvent(userId, requestFile.getMachineId(), upload);
        }
        // 执行上传
        for (FileTransferLogDO uploadFile : uploadFiles) {
            IFileTransferProcessor.of(FileTransferHint.transfer(uploadFile)).exec();
        }
        // 设置日志参数
        List<Long> machineIdList = requestFiles.stream()
                .map(FileUploadRequest::getMachineId)
                .distinct()
                .collect(Collectors.toList());
        List<String> remoteFiles = requestFiles.stream()
                .map(FileUploadRequest::getRemotePath)
                .distinct()
                .collect(Collectors.toList());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID_LIST, machineIdList);
        EventParamsHolder.addParam(EventKeys.PATHS, remoteFiles);
        EventParamsHolder.addParam(EventKeys.COUNT, requestFiles.size());
        // 返回
        return requestFiles.stream()
                .map(FileUploadRequest::getFileToken)
                .collect(Collectors.toList());
    }

}
