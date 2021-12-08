package com.orion.ops.service.api;

import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.entity.dto.FileDownloadDTO;

/**
 * 文件下载service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:21
 */
public interface FileDownloadService {

    /**
     * 下载文件 检查文件是否存在
     *
     * @param id   id
     * @param type type
     * @return token
     */
    String getDownloadToken(Long id, FileDownloadType type);

    /**
     * 通过token获取下载文件路径
     *
     * @param token token
     * @return path
     */
    FileDownloadDTO getPathByDownloadToken(String token);

}
