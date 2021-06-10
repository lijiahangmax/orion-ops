package com.orion.ops.service.api;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.DownloadType;

/**
 * 文件下载service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:21
 */
public interface FileDownloadService {

    /**
     * 检查文件是否存在
     *
     * @param id   id
     * @param type type
     * @return token
     */
    HttpWrapper<String> checkFile(Long id, DownloadType type);

    /**
     * 通过token获取文件路径
     *
     * @param token token
     * @return path
     */
    String getPathByToken(String token);

}
