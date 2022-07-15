package com.orion.ops.controller;

import com.orion.lang.id.ObjectIds;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.entity.dto.SftpUploadInfoDTO;
import com.orion.ops.entity.request.BatchUploadRequest;
import com.orion.ops.entity.request.sftp.FileUploadRequest;
import com.orion.ops.entity.vo.BatchUploadCheckVO;
import com.orion.ops.entity.vo.BatchUploadTokenVO;
import com.orion.ops.service.api.BatchUploadService;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
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
