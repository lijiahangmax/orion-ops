package com.orion.ops.service.api;

import com.orion.ops.entity.domain.FileTransferLogDO;
import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.FileTransferLogVO;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.remote.channel.sftp.SftpExecutor;

import java.util.List;

/**
 * sftp api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:41
 */
public interface SftpService {

    /**
     * 打开sftp连接
     *
     * @param machineId 机器id
     * @return FileOpenVO
     */
    FileOpenVO open(Long machineId);

    /**
     * 文件列表
     *
     * @param request request
     * @return list
     */
    FileListVO list(FileListRequest request);

    /**
     * 文件夹列表
     *
     * @param request request
     * @return list
     */
    FileListVO listDir(FileListRequest request);

    /**
     * mkdir
     *
     * @param request request
     * @return 文件目录
     */
    String mkdir(FileMkdirRequest request);

    /**
     * touch
     *
     * @param request request
     * @return 文件目录
     */
    String touch(FileTouchRequest request);

    /**
     * truncate
     *
     * @param request request
     */
    void truncate(FileTruncateRequest request);

    /**
     * mv
     *
     * @param request request
     * @return 移动后的位置
     */
    String move(FileMoveRequest request);

    /**
     * rm -rf
     *
     * @param request request
     */
    void remove(FileRemoveRequest request);

    /**
     * chmod
     *
     * @param request request
     * @return 权限字符串
     */
    String chmod(FileChmodRequest request);

    /**
     * chown
     *
     * @param request request
     */
    void chown(FileChownRequest request);

    /**
     * chgrp
     *
     * @param request request
     */
    void changeGroup(FileChangeGroupRequest request);

    /**
     * 检查文件是否存在
     *
     * @param request request
     * @return 存在的文件名称
     */
    List<String> checkFilePresent(FilePresentCheckRequest request);

    /**
     * 获取上传文件accessToken
     *
     * @param sessionToken sessionToken
     * @return accessToken
     */
    String getUploadAccessToken(String sessionToken);

    /**
     * 检查上传token
     *
     * @param accessToken accessToken
     * @return machineId
     */
    Long checkUploadAccessToken(String accessToken);

    /**
     * 上传文件
     *
     * @param requestFiles requestFiles
     * @param machineId    machineId
     */
    void upload(Long machineId, List<FileUploadRequest> requestFiles);

    /**
     * 下载文件
     *
     * @param request request
     */
    void download(FileDownloadRequest request);

    /**
     * 传输暂停
     *
     * @param fileToken fileToken
     */
    void transferPause(String fileToken);

    /**
     * 传输恢复
     *
     * @param fileToken fileToken
     */
    void transferResume(String fileToken);

    /**
     * 传输失败重试
     *
     * @param fileToken fileToken
     */
    void transferRetry(String fileToken);

    /**
     * 传输暂停
     *
     * @param sessionToken sessionToken
     */
    void transferPauseAll(String sessionToken);

    /**
     * 传输恢复
     *
     * @param sessionToken sessionToken
     */
    void transferResumeAll(String sessionToken);

    /**
     * 传输失败重试
     *
     * @param sessionToken sessionToken
     */
    void transferRetryAll(String sessionToken);

    /**
     * 传输列表
     *
     * @param machineId 机器id
     * @return rows
     */
    List<FileTransferLogVO> transferList(Long machineId);

    /**
     * 传输删除(单个)
     *
     * @param fileToken fileToken
     */
    void transferRemove(String fileToken);

    /**
     * 传输清空(全部)
     *
     * @param machineId machineId
     * @return effect
     */
    Integer transferClear(Long machineId);

    /**
     * 获取sftp下载文件本地路径
     *
     * @param id id
     * @return FileTransferLogDO
     */
    FileTransferLogDO getDownloadFilePath(Long id);

    /**
     * 通过token获取 SftpExecutor
     *
     * @param sessionToken sessionToken
     * @return SftpExecutor
     */
    SftpExecutor getBasicExecutorByToken(String sessionToken);

    /**
     * 获取机器id
     *
     * @param sessionToken sessionToken
     * @return 机器id
     */
    Long getMachineId(String sessionToken);

    /**
     * 获取token信息
     *
     * @param sessionToken sessionToken
     * @return userId machineId
     */
    Long[] getTokenInfo(String sessionToken);

}
