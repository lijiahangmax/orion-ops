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
     * ls
     *
     * @param request request
     * @return list
     */
    FileListVO list(FileListRequest request);

    /**
     * ls
     *
     * @param path     path
     * @param all      0查询普通文件 1查询隐藏文件
     * @param executor executor
     * @return list
     */
    FileListVO list(String path, int all, SftpExecutor executor);

    /**
     * mkdir
     *
     * @param request request
     */
    void mkdir(FileMkdirRequest request);

    /**
     * touch
     *
     * @param request request
     */
    void touch(FileTouchRequest request);

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
     */
    void move(FileMoveRequest request);

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
     */
    void chmod(FileChmodRequest request);

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
     * 获取上传文件token
     *
     * @param sessionToken sessionToken
     * @return fileToken
     */
    String getUploadToken(String sessionToken);

    /**
     * 检查上传token
     *
     * @param fileToken fileToken
     * @return machineId
     */
    Long checkUploadToken(String fileToken);

    /**
     * 上传文件
     *
     * @param request request
     * @return fileToken
     */
    String upload(FileUploadRequest request);

    /**
     * 下载文件
     *
     * @param request request
     * @return fileToken
     */
    String download(FileDownloadRequest request);

    /**
     * 传输列表
     *
     * @param machineId 机器id
     * @return rows
     */
    List<FileTransferLogVO> transferList(Long machineId);

    /**
     * 传输暂停
     *
     * @param fileToken fileToken
     */
    void transferStop(String fileToken);

    /**
     * 传输恢复
     *
     * @param fileToken fileToken
     */
    void transferResume(String fileToken);

    /**
     * 传输删除(单个)
     *
     * @param fileToken fileToken
     * @return effect
     */
    Integer transferRemove(String fileToken);

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
     * 获取token信息
     *
     * @param sessionToken sessionToken
     * @return userId machineId
     */
    Long[] getTokenInfo(String sessionToken);

}
