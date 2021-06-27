package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.ops.service.api.SftpService;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.sftp.SftpExecutor;
import com.orion.utils.io.Files1;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public FileListVO mkdir(@RequestBody FileMkdirRequest request) {
        this.checkNormalize(request.getCurrent());
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        sftpService.mkdir(request);
        return this.ll(request);
    }

    /**
     * 创建文件
     */
    @RequestMapping("/touch")
    public FileListVO touch(@RequestBody FileTouchRequest request) {
        this.checkNormalize(request.getCurrent());
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        sftpService.touch(request);
        return this.ll(request);
    }

    /**
     * 截断文件
     */
    @RequestMapping("/truncate")
    public FileListVO truncate(@RequestBody FileTruncateRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        sftpService.truncate(request);
        return this.ll(request);
    }

    /**
     * 移动
     */
    @RequestMapping("/move")
    public FileListVO touch(@RequestBody FileMoveRequest request) {
        this.checkNormalize(request.getSource());
        Valid.notBlank(request.getTarget());
        this.setExecutor(request);
        sftpService.move(request);
        return this.ll(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/remove")
    public FileListVO remove(@RequestBody FileRemoveRequest request) {
        this.checkNormalize(request.getPath());
        Valid.isFalse(Const.SLASH.equals(Files1.getPath(request.getPath().trim())), MessageConst.DEL_ROOT_PATH);
        this.setExecutor(request);
        sftpService.remove(request);
        return this.ll(request);
    }

    /**
     * 修改权限
     */
    @RequestMapping("/chmod")
    public FileListVO chmod(@RequestBody FileChmodRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getPermission());
        this.setExecutor(request);
        sftpService.chmod(request);
        return this.ll(request);
    }

    /**
     * 修改所有者
     */
    @RequestMapping("/chown")
    public FileListVO chown(@RequestBody FileChownRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getUid());
        this.setExecutor(request);
        sftpService.chown(request);
        return this.ll(request);
    }

    /**
     * 修改所有组
     */
    @RequestMapping("/chgrp")
    public FileListVO changeGroup(@RequestBody FileChangeGroupRequest request) {
        this.checkNormalize(request.getPath());
        Valid.notNull(request.getGid());
        this.setExecutor(request);
        sftpService.changeGroup(request);
        return this.ll(request);
    }

    /**
     * 下载文件
     */
    @RequestMapping("/transfer/download/exec")
    public String download(@RequestBody FileDownloadRequest request) {
        this.checkNormalize(request.getPath());
        this.setExecutor(request);
        return sftpService.download(request);
    }

    /**
     * 传输恢复
     */
    @RequestMapping("/transfer/download/resume")
    public HttpWrapper<?> transferResume(@RequestBody FileTransferResumeRequest request) {
        String token = Valid.notBlank(request.getToken());
        sftpService.downloadResume(token);
        return HttpWrapper.ok();
    }

    /**
     * 传输列表
     */
    @RequestMapping("/transfer/list")
    public HttpWrapper<?> transferList(@RequestBody FileTransferResumeRequest request) {
        return HttpWrapper.ok();
    }

    /**
     * 传输暂停
     */
    @RequestMapping("/transfer/stop")
    public HttpWrapper<?> transferStop(@RequestBody FileTransferStopRequest request) {
        String token = Valid.notBlank(request.getToken());
        sftpService.transferStop(token);
        return HttpWrapper.ok();
    }

    /**
     * 查询列表
     */
    private FileListVO ll(FileBaseRequest request) {
        return sftpService.list(request.getCurrent(), request.getAll(), request.getExecutor());
    }

    /**
     * 通过token 设置 SftpExecutor
     *
     * @param request FileBaseRequest
     */
    private void setExecutor(FileBaseRequest request) {
        SftpExecutor executor = sftpService.getBasicExecutorByToken(request.getToken());
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

    // 传输列表
    // download
    // upload

}
