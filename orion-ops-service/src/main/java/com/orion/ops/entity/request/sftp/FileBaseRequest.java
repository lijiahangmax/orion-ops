package com.orion.ops.entity.request.sftp;

import lombok.Data;

/**
 * sftp 请求request
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:28
 */
@Data
public class FileBaseRequest {

    /**
     * sessionToken
     */
    private String sessionToken;

    /**
     * 是否查询隐藏文件
     */
    private Boolean all;

}
