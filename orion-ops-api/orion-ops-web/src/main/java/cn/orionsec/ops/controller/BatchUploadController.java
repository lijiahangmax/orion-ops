/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.id.ObjectIds;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.dto.sftp.SftpUploadInfoDTO;
import cn.orionsec.ops.entity.request.sftp.FileUploadRequest;
import cn.orionsec.ops.entity.request.upload.BatchUploadRequest;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadTokenVO;
import cn.orionsec.ops.service.api.BatchUploadService;
import cn.orionsec.ops.service.api.SftpService;
import cn.orionsec.ops.utils.PathBuilders;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 批量上传 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 15:42
 */
@Api(tags = "批量上传")
@RestController
@RestWrapper
@RequestMapping("/orion/api/batch-upload")
public class BatchUploadController {

    @Resource
    private BatchUploadService batchUploadService;

    @Resource
    private SftpService sftpService;

    @PostMapping("/check")
    @ApiOperation(value = "检查机器以及文件")
    public BatchUploadCheckVO checkFilePresent(@RequestBody BatchUploadRequest request) {
        Valid.checkNormalize(request.getRemotePath());
        Valid.notEmpty(request.getMachineIds());
        Valid.notEmpty(request.getNames());
        Valid.checkUploadSize(request.getSize());
        return batchUploadService.checkMachineFiles(request);
    }

    @PostMapping("/token")
    @ApiOperation(value = "获取上传token")
    public BatchUploadTokenVO getUploadAccessToken(@RequestBody BatchUploadRequest request) {
        Valid.checkNormalize(request.getRemotePath());
        Valid.notEmpty(request.getMachineIds());
        return batchUploadService.getUploadAccessToken(request);
    }

    @PostMapping("/exec")
    @ApiOperation(value = "执行上传")
    @EventLog(EventType.SFTP_UPLOAD)
    public List<String> uploadFile(@RequestParam("accessToken") String accessToken, @RequestParam("files") List<MultipartFile> files) throws IOException {
        // 检查文件
        Valid.notBlank(accessToken);
        Valid.notEmpty(files);
        // 检查token
        SftpUploadInfoDTO uploadInfo = sftpService.checkUploadAccessToken(accessToken);
        String remotePath = uploadInfo.getRemotePath();
        List<Long> machineIdList = uploadInfo.getMachineIdList();

        List<FileUploadRequest> requestFiles = Lists.newList();
        for (Long machineId : machineIdList) {
            for (MultipartFile file : files) {
                // 传输文件到本地
                String fileToken = ObjectIds.nextId();
                String localPath = PathBuilders.getSftpUploadFilePath(fileToken);
                Path localAbsolutePath = Paths.get(SystemEnvAttr.SWAP_PATH.getValue(), localPath);
                Files1.touch(localAbsolutePath);
                file.transferTo(localAbsolutePath);

                // 请求参数
                FileUploadRequest request = new FileUploadRequest();
                request.setMachineId(machineId);
                request.setLocalPath(localPath);
                request.setFileToken(fileToken);
                request.setRemotePath(Files1.getPath(remotePath, file.getOriginalFilename()));
                request.setSize(file.getSize());
                requestFiles.add(request);
            }
        }
        // 提交任务
        return batchUploadService.batchUpload(requestFiles);
    }

}
