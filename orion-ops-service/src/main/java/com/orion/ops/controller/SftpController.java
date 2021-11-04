package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.sftp.SftpExecutor;
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
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        return sftpService.list(request);
    }

    /**
     * 创建文件夹
     */
    @RequestMapping("/mkdir")
    public String mkdir(@RequestBody FileMkdirRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        return sftpService.mkdir(request);
    }

    /**
     * 创建文件
     */
    @RequestMapping("/touch")
    public String touch(@RequestBody FileTouchRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        return sftpService.touch(request);
    }

    /**
     * 截断文件
     */
    @RequestMapping("/truncate")
    public void truncate(@RequestBody FileTruncateRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        sftpService.truncate(request);
    }

    /**
     * 移动
     */
    @RequestMapping("/move")
    public String move(@RequestBody FileMoveRequest request) {
        this.checkNormalize(request.getSource());
        Valid.notBlank(request.getTarget());
        this.setExecutor(request);
        return sftpService.move(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/remove")
    public void remove(@RequestBody FileRemoveRequest request) {
        this.checkNormalize(request.getPath());
        Valid.isFalse(Const.SLASH.equals(Files1.getPath(request.getPath().trim())), MessageConst.DEL_ROOT_PATH);
        this.setExecutor(request);
        sftpService.remove(request);
    }

    /**
     * 修改权限
     */
    @RequestMapping("/chmod")
    public String chmod(@RequestBody FileChmodRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getPermission());
        this.setExecutor(request);
        return sftpService.chmod(request);
    }

    /**
     * 修改所有者
     */
    @RequestMapping("/chown")
    public void chown(@RequestBody FileChownRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getUid());
        this.setExecutor(request);
        sftpService.chown(request);
    }

    /**
     * 修改所有组
     */
    @RequestMapping("/chgrp")
    public void changeGroup(@RequestBody FileChangeGroupRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getGid());
        this.setExecutor(request);
        sftpService.changeGroup(request);
    }

    /**
     * 检查文件是否存在
     */
    @RequestMapping("/check/present")
    public boolean checkFilePresent(@RequestBody FilePresentCheckRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notBlank(request.getName());
        this.setExecutor(request);
        return sftpService.checkFilePresent(request);
    }

    /**
     * 获取上传文件fileToken
     */
    @RequestMapping("/upload/{sessionToken}/token")
    public String getUploadToken(@PathVariable String sessionToken) {
        return sftpService.getUploadToken(sessionToken);
    }

    /**
     * 上传文件
     */
    @RequestMapping("/upload/exec")
    public String uploadFile(@RequestParam("fileToken") String fileToken, @RequestParam("remotePath") String remotePath,
                             @RequestParam("file") MultipartFile file) throws IOException {
        // 检查路径
        this.checkNormalize(remotePath);
        Valid.notBlank(fileToken);
        Valid.notNull(file);
        // 检查token
        Long machineId = sftpService.checkUploadToken(fileToken);
        // 传输文件到本地
        String localPath = PathBuilders.getSftpUploadFilePath(fileToken);
        Path localAbsolutePath = Paths.get(MachineEnvAttr.SWAP_PATH.getValue(), localPath);
        Files1.touch(localAbsolutePath);
        file.transferTo(localAbsolutePath);

        // 提交任务
        FileUploadRequest request = new FileUploadRequest();
        request.setMachineId(machineId);
        request.setFileToken(fileToken);
        request.setLocalPath(localPath);
        request.setRemotePath(Files1.getPath(remotePath + "/" + file.getOriginalFilename()));
        request.setSize(file.getSize());
        return sftpService.upload(request);
    }

    /**
     * 下载文件
     */
    @RequestMapping("/download/exec")
    public String downloadFile(@RequestBody FileDownloadRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        return sftpService.download(request);
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
     * 传输暂停
     */
    @RequestMapping("/transfer/pause")
    public HttpWrapper<?> transferStop(@RequestBody FileTransferStopRequest request) {
        String fileToken = Valid.notBlank(request.getFileToken());
        sftpService.transferStop(fileToken);
        return HttpWrapper.ok();
    }

    /**
     * 传输恢复
     */
    @RequestMapping("/transfer/resume")
    public HttpWrapper<?> transferResume(@RequestBody FileTransferResumeRequest request) {
        String fileToken = Valid.notBlank(request.getFileToken());
        sftpService.transferResume(fileToken);
        return HttpWrapper.ok();
    }

    /**
     * 传输删除(单个)
     */
    @RequestMapping("/transfer/remove")
    public Integer transferRemove(@RequestBody FileTransferRemoveRequest request) {
        String fileToken = Valid.notBlank(request.getFileToken());
        return sftpService.transferRemove(fileToken);
    }

    /**
     * 传输清空(全部)
     */
    @RequestMapping("/transfer/{sessionToken}/clear")
    public Integer transferRemove(@PathVariable("sessionToken") String sessionToken) {
        Long[] tokenInfo = sftpService.getTokenInfo(sessionToken);
        Valid.isTrue(Currents.getUserId().equals(tokenInfo[0]));
        return sftpService.transferClear(tokenInfo[1]);
    }

    /**
     * 查询文件列表
     */
    private FileListVO ll(FileBaseRequest request) {
        return sftpService.list(request.getCurrent(), request.getAll(), request.getExecutor());
    }

    /**
     * 通过sessionToken 获取 SftpExecutor
     *
     * @param request FileBaseRequest
     */
    private void setExecutor(FileBaseRequest request) {
        SftpExecutor executor = sftpService.getBasicExecutorByToken(request.getSessionToken());
        request.setExecutor(executor);
    }

    /**
     * 检查路径是否合法化 即不包含 ./ ../
     *
     * @param path path
     */
    private void checkNormalize(String path) {
        Valid.notBlank(path);
        Valid.isTrue(Files1.isNormalize(path), MessageConst.PATH_NOT_NORMALIZE);
    }

}
