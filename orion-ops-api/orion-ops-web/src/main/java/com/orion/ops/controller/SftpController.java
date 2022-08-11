package com.orion.ops.controller;

import com.orion.lang.id.ObjectIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.Files1;
import com.orion.net.base.file.sftp.SftpErrorMessage;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.sftp.SftpPackageType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.dto.sftp.SftpSessionTokenDTO;
import com.orion.ops.entity.dto.sftp.SftpUploadInfoDTO;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.sftp.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
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
 * sftp 操作 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/22 19:53
 */
@Api(tags = "sftp操作")
@RestController
@RestWrapper
@RequestMapping("/orion/api/sftp")
public class SftpController {

    @Resource
    private SftpService sftpService;

    @PostMapping("/open")
    @ApiOperation(value = "打开sftp")
    @EventLog(EventType.OPEN_SFTP)
    public FileOpenVO open(@RequestBody FileOpenRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return sftpService.open(machineId);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取文件列表")
    public FileListVO list(@RequestBody FileListRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            return sftpService.list(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/list-dir")
    @ApiOperation(value = "获取文件夹列表")
    public FileListVO listDir(@RequestBody FileListRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            return sftpService.listDir(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/mkdir")
    @ApiOperation(value = "创建文件夹")
    @EventLog(EventType.SFTP_MKDIR)
    public String mkdir(@RequestBody FileMkdirRequest request) {
        Valid.checkNormalize(request.getPath());
        return sftpService.mkdir(request);
    }

    @PostMapping("/touch")
    @ApiOperation(value = "创建文件")
    @EventLog(EventType.SFTP_TOUCH)
    public String touch(@RequestBody FileTouchRequest request) {
        Valid.checkNormalize(request.getPath());
        return sftpService.touch(request);
    }

    @PostMapping("/truncate")
    @ApiOperation(value = "截断文件")
    @EventLog(EventType.SFTP_TRUNCATE)
    public void truncate(@RequestBody FileTruncateRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            sftpService.truncate(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/move")
    @ApiOperation(value = "移动文件")
    @EventLog(EventType.SFTP_MOVE)
    public String move(@RequestBody FileMoveRequest request) {
        Valid.checkNormalize(request.getSource());
        Valid.notBlank(request.getTarget());
        try {
            return sftpService.move(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/remove")
    @ApiOperation(value = "删除文件/文件夹")
    @EventLog(EventType.SFTP_REMOVE)
    public void remove(@RequestBody FileRemoveRequest request) {
        List<String> paths = Valid.notEmpty(request.getPaths());
        paths.forEach(Valid::checkNormalize);
        boolean isSafe = paths.stream().noneMatch(Const.UNSAFE_FS_DIR::contains);
        Valid.isSafe(isSafe);
        try {
            sftpService.remove(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/chmod")
    @ApiOperation(value = "修改权限")
    @EventLog(EventType.SFTP_CHMOD)
    public String chmod(@RequestBody FileChmodRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getPermission());
        try {
            return sftpService.chmod(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/chown")
    @ApiOperation(value = "修改所有者")
    @EventLog(EventType.SFTP_CHOWN)
    public void chown(@RequestBody FileChownRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getUid());
        try {
            sftpService.chown(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/chgrp")
    @ApiOperation(value = "修改所有组")
    @EventLog(EventType.SFTP_CHGRP)
    public void changeGroup(@RequestBody FileChangeGroupRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getGid());
        try {
            sftpService.changeGroup(request);
        } catch (RuntimeException e) {
            throw this.convertError(e);
        }
    }

    @PostMapping("/check-present")
    @ApiOperation(value = "检查文件是否存在")
    public List<String> checkFilePresent(@RequestBody FilePresentCheckRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notEmpty(request.getNames());
        Valid.checkUploadSize(request.getSize());
        return sftpService.checkFilePresent(request);
    }

    @PostMapping("/upload/token")
    @ApiOperation(value = "获取上传文件accessToken")
    public String getUploadAccessToken(@RequestBody FileUploadRequest request) {
        return sftpService.getUploadAccessToken(request);
    }

    @PostMapping("/upload/exec")
    @ApiOperation(value = "上传文件")
    @EventLog(EventType.SFTP_UPLOAD)
    public void uploadFile(@RequestParam("accessToken") String accessToken, @RequestParam("files") List<MultipartFile> files) throws IOException {
        // 检查文件
        Valid.notBlank(accessToken);
        Valid.notEmpty(files);
        // 检查token
        SftpUploadInfoDTO uploadInfo = sftpService.checkUploadAccessToken(accessToken);
        Long machineId = uploadInfo.getMachineId();
        String remotePath = uploadInfo.getRemotePath();

        List<FileUploadRequest> requestFiles = Lists.newList();
        for (MultipartFile file : files) {
            // 传输文件到本地
            String fileToken = ObjectIds.nextId();
            String localPath = PathBuilders.getSftpUploadFilePath(fileToken);
            Path localAbsolutePath = Paths.get(SystemEnvAttr.SWAP_PATH.getValue(), localPath);
            Files1.touch(localAbsolutePath);
            file.transferTo(localAbsolutePath);

            // 提交任务
            FileUploadRequest request = new FileUploadRequest();
            request.setMachineId(machineId);
            request.setFileToken(fileToken);
            request.setLocalPath(localPath);
            request.setRemotePath(Files1.getPath(remotePath, file.getOriginalFilename()));
            request.setSize(file.getSize());
            requestFiles.add(request);
        }
        sftpService.upload(machineId, requestFiles);
    }

    @PostMapping("/download/exec")
    @ApiOperation(value = "下载文件")
    @EventLog(EventType.SFTP_DOWNLOAD)
    public void downloadFile(@RequestBody FileDownloadRequest request) {
        List<String> paths = Valid.notEmpty(request.getPaths());
        paths.forEach(Valid::checkNormalize);
        sftpService.download(request);
    }

    @PostMapping("/package-download/exec")
    @ApiOperation(value = "打包下载文件")
    @EventLog(EventType.SFTP_DOWNLOAD)
    public void packageDownloadFile(@RequestBody FileDownloadRequest request) {
        List<String> paths = Valid.notEmpty(request.getPaths());
        paths.forEach(Valid::checkNormalize);
        sftpService.packageDownload(request);
    }

    @GetMapping("/transfer/{fileToken}/pause")
    @ApiOperation(value = "暂停文件传输")
    public void transferPause(@PathVariable("fileToken") String fileToken) {
        sftpService.transferPause(fileToken);
    }

    @GetMapping("/transfer/{fileToken}/resume")
    @ApiOperation(value = "恢复文件传输")
    public void transferResume(@PathVariable("fileToken") String fileToken) {
        sftpService.transferResume(fileToken);
    }

    @GetMapping("/transfer/{fileToken}/retry")
    @ApiOperation(value = "传输失败重试")
    public void transferRetry(@PathVariable("fileToken") String fileToken) {
        sftpService.transferRetry(fileToken);
    }

    @GetMapping("/transfer/{fileToken}/re-upload")
    @ApiOperation(value = "重新上传文件")
    public void transferReUpload(@PathVariable("fileToken") String fileToken) {
        sftpService.transferReUpload(fileToken);
    }

    @GetMapping("/transfer/{fileToken}/re-download")
    @ApiOperation(value = "重新下载文件")
    public void transferReDownload(@PathVariable("fileToken") String fileToken) {
        sftpService.transferReDownload(fileToken);
    }

    @GetMapping("/transfer/{sessionToken}/pause-all")
    @ApiOperation(value = "暂停所有传输")
    public void transferPauseAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferPauseAll(sessionToken);
    }

    @GetMapping("/transfer/{sessionToken}/resume-all")
    @ApiOperation(value = "恢复所有传输")
    public void transferResumeAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferResumeAll(sessionToken);
    }

    @GetMapping("/transfer/{sessionToken}/retry-all")
    @ApiOperation(value = "失败重试所有")
    public void transferRetryAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferRetryAll(sessionToken);
    }

    @GetMapping("/transfer/{sessionToken}/list")
    @ApiOperation(value = "获取传输列表")
    public List<FileTransferLogVO> transferList(@PathVariable("sessionToken") String sessionToken) {
        SftpSessionTokenDTO tokenInfo = sftpService.getTokenInfo(sessionToken);
        Valid.isTrue(Currents.getUserId().equals(tokenInfo.getUserId()));
        return sftpService.transferList(tokenInfo.getMachineId());
    }

    @GetMapping("/transfer/{fileToken}/remove")
    @ApiOperation(value = "删除单个传输记录 (包含进行中的)")
    public void transferRemove(@PathVariable("fileToken") String fileToken) {
        sftpService.transferRemove(fileToken);
    }

    @GetMapping("/transfer/{sessionToken}/clear")
    @ApiOperation(value = "清空全部传输记录 (不包含进行中的)")
    public Integer transferClear(@PathVariable("sessionToken") String sessionToken) {
        SftpSessionTokenDTO tokenInfo = sftpService.getTokenInfo(sessionToken);
        Valid.isTrue(Currents.getUserId().equals(tokenInfo.getUserId()));
        return sftpService.transferClear(tokenInfo.getMachineId());
    }

    @GetMapping("/transfer/{sessionToken}/{packageType}/package")
    @ApiOperation(value = "传输打包全部已完成未删除的文件")
    @EventLog(EventType.SFTP_PACKAGE)
    public void transferPackage(@PathVariable("sessionToken") String sessionToken, @PathVariable("packageType") Integer packageType) {
        SftpPackageType sftpPackageType = Valid.notNull(SftpPackageType.of(packageType));
        sftpService.transferPackage(sessionToken, sftpPackageType);
    }

    /**
     * 检测文件是否存在
     *
     * @param e e
     * @return RuntimeException
     */
    private RuntimeException convertError(RuntimeException e) {
        if (SftpErrorMessage.NO_SUCH_FILE.isCause(e)) {
            return Exceptions.argument(MessageConst.NO_SUCH_FILE);
        } else {
            return e;
        }
    }

}
