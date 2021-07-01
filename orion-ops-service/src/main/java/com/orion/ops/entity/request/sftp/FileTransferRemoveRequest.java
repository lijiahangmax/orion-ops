package com.orion.ops.entity.request.sftp;

import lombok.Data;

/**
 * 文件恢复清空请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 12:12
 */
@Data
public class FileTransferRemoveRequest {

    /**
     * fileToken
     */
    private String fileToken;

}
