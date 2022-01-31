package com.orion.ops.controller;

import com.orion.id.ObjectIds;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.sftp.SftpPackageType;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.collect.Lists;
import com.orion.utils.io.Files1;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * sftp 操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/22 19:53
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/sftp")
public class SftpController {

    @Resource
    private SftpService sftpService;

    /**
     * 打开
     */
    @RequestMapping("/open")
    public FileOpenVO open(@RequestBody FileOpenRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return sftpService.open(machineId);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public FileListVO list(@RequestBody FileListRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            return sftpService.list(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 文件夹列表
     */
    @RequestMapping("/list-dir")
    public FileListVO listDir(@RequestBody FileListRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            return sftpService.listDir(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 创建文件夹
     */
    @RequestMapping("/mkdir")
    @EventLog(EventType.SFTP_MKDIR)
    public String mkdir(@RequestBody FileMkdirRequest request) {
        Valid.checkNormalize(request.getPath());
        return sftpService.mkdir(request);
    }

    /**
     * 创建文件
     */
    @RequestMapping("/touch")
    @EventLog(EventType.SFTP_TOUCH)
    public String touch(@RequestBody FileTouchRequest request) {
        Valid.checkNormalize(request.getPath());
        return sftpService.touch(request);
    }

    /**
     * 截断文件
     */
    @RequestMapping("/truncate")
    @EventLog(EventType.SFTP_TRUNCATE)
    public void truncate(@RequestBody FileTruncateRequest request) {
        Valid.checkNormalize(request.getPath());
        try {
            sftpService.truncate(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 移动
     */
    @RequestMapping("/move")
    @EventLog(EventType.SFTP_MOVE)
    public String move(@RequestBody FileMoveRequest request) {
        Valid.checkNormalize(request.getSource());
        Valid.notBlank(request.getTarget());
        try {
            return sftpService.move(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/remove")
    @EventLog(EventType.SFTP_REMOVE)
    public void remove(@RequestBody FileRemoveRequest request) {
        List<String> paths = Valid.notEmpty(request.getPaths());
        paths.forEach(Valid::checkNormalize);
        boolean isSafe = paths.stream().noneMatch(Const.UNSAFE_FS_DIR::contains);
        Valid.isSafe(isSafe);
        try {
            sftpService.remove(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 修改权限
     */
    @RequestMapping("/chmod")
    @EventLog(EventType.SFTP_CHMOD)
    public String chmod(@RequestBody FileChmodRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getPermission());
        try {
            return sftpService.chmod(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 修改所有者
     */
    @RequestMapping("/chown")
    @EventLog(EventType.SFTP_CHOWN)
    public void chown(@RequestBody FileChownRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getUid());
        try {
            sftpService.chown(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 修改所有组
     */
    @RequestMapping("/chgrp")
    @EventLog(EventType.SFTP_CHGRP)
    public void changeGroup(@RequestBody FileChangeGroupRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notNull(request.getGid());
        try {
            sftpService.changeGroup(request);
        } catch (RuntimeException e) {
            throw convertError(e);
        }
    }

    /**
     * 检查文件是否存在
     */
    @RequestMapping("/check/present")
    public List<String> checkFilePresent(@RequestBody FilePresentCheckRequest request) {
        Valid.checkNormalize(request.getPath());
        Valid.notEmpty(request.getNames());
        return sftpService.checkFilePresent(request);
    }

    /**
     * 获取上传文件accessToken
     */
    @RequestMapping("/upload/{sessionToken}/token")
    public String getUploadAccessToken(@PathVariable String sessionToken) {
        return sftpService.getUploadAccessToken(sessionToken);
    }

    /**
     * 上传文件
     */
    @RequestMapping("/upload/exec")
    @EventLog(EventType.SFTP_UPLOAD)
    public void uploadFile(@RequestParam("accessToken") String accessToken, @RequestParam("remotePath") String remotePath,
                           @RequestParam("files") List<MultipartFile> files) throws IOException {
        // 检查路径
        Valid.checkNormalize(remotePath);
        Valid.notBlank(accessToken);
        Valid.notEmpty(files);
        // 检查token
        Long machineId = sftpService.checkUploadAccessToken(accessToken);

        List<FileUploadRequest> requestFiles = Lists.newList();
        for (MultipartFile file : files) {
            // 传输文件到本地
            String fileToken = ObjectIds.next();
            String localPath = PathBuilders.getSftpUploadFilePath(fileToken);
            Path localAbsolutePath = Paths.get(MachineEnvAttr.SWAP_PATH.getValue(), localPath);
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

    /**
     * 下载文件
     */
    @RequestMapping("/download/exec")
    @EventLog(EventType.SFTP_DOWNLOAD)
    public void downloadFile(@RequestBody FileDownloadRequest request) {
        List<String> paths = Valid.notEmpty(request.getPaths());
        paths.forEach(Valid::checkNormalize);
        sftpService.download(request);
    }

    /**
     * 传输暂停
     */
    @RequestMapping("/transfer/{fileToken}/pause")
    public void transferPause(@PathVariable("fileToken") String fileToken) {
        sftpService.transferPause(fileToken);
    }

    /**
     * 传输恢复
     */
    @RequestMapping("/transfer/{fileToken}/resume")
    public void transferResume(@PathVariable("fileToken") String fileToken) {
        sftpService.transferResume(fileToken);
    }

    /**
     * 传输失败重试
     */
    @RequestMapping("/transfer/{fileToken}/retry")
    public void transferRetry(@PathVariable("fileToken") String fileToken) {
        sftpService.transferRetry(fileToken);
    }

    /**
     * 重新上传
     */
    @RequestMapping("/transfer/{fileToken}/re/upload")
    public void transferReUpload(@PathVariable("fileToken") String fileToken) {
        sftpService.transferReUpload(fileToken);
    }

    /**
     * 重新下载
     */
    @RequestMapping("/transfer/{fileToken}/re/download")
    public void transferReDownload(@PathVariable("fileToken") String fileToken) {
        sftpService.transferReDownload(fileToken);
    }

    /**
     * 批量暂停所有传输
     */
    @RequestMapping("/transfer/{sessionToken}/pause/all")
    public void transferPauseAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferPauseAll(sessionToken);
    }

    /**
     * 批量恢复所有传输
     */
    @RequestMapping("/transfer/{sessionToken}/resume/all")
    public void transferResumeAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferResumeAll(sessionToken);
    }

    /**
     * 批量失败重试所有
     */
    @RequestMapping("/transfer/{sessionToken}/retry/all")
    public void transferRetryAll(@PathVariable("sessionToken") String sessionToken) {
        sftpService.transferRetryAll(sessionToken);
    }

    /**
     * 传输列表
     */
    @RequestMapping("/transfer/{sessionToken}/list")
    public List<FileTransferLogVO> transferList(@PathVariable("sessionToken") String sessionToken) {
        Long[] tokenInfo = sftpService.getTokenInfo(sessionToken);
        Valid.isTrue(Currents.getUserId().equals(tokenInfo[0]));
        return sftpService.transferList(tokenInfo[1]);
    }

    /**
     * 传输删除(单个) 包含进行中的
     */
    @RequestMapping("/transfer/{fileToken}/remove")
    public void transferRemove(@PathVariable("fileToken") String fileToken) {
        sftpService.transferRemove(fileToken);
    }

    /**
     * 传输清空(全部) 不包含进行中的
     */
    @RequestMapping("/transfer/{sessionToken}/clear")
    public Integer transferClear(@PathVariable("sessionToken") String sessionToken) {
        Long[] tokenInfo = sftpService.getTokenInfo(sessionToken);
        Valid.isTrue(Currents.getUserId().equals(tokenInfo[0]));
        return sftpService.transferClear(tokenInfo[1]);
    }

    /**
     * 传输打包 全部已完成未删除的文件
     */
    @RequestMapping("/transfer/{sessionToken}/{packageType}/package")
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
        if (e.getMessage().toLowerCase().contains(Const.NO_SUCH_FILE)) {
            return Exceptions.argument(MessageConst.NO_SUCH_FILE);
        } else {
            return e;
        }
    }

}
