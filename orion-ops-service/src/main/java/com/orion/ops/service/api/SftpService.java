package com.orion.ops.service.api;

import com.orion.ops.entity.request.sftp.*;
import com.orion.ops.entity.vo.sftp.FileListVO;
import com.orion.ops.entity.vo.sftp.FileOpenVO;
import com.orion.remote.channel.sftp.SftpExecutor;

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
     * 通过token获取 SftpExecutor
     *
     * @param token token
     * @return SftpExecutor
     */
    SftpExecutor getExecutorByToken(String token);

}
