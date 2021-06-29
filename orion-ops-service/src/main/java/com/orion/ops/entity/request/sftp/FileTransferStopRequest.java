package com.orion.ops.entity.request.sftp;

import lombok.Data;

/**
 * 文件恢复传输请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 12:07
 */
@Data
public class FileTransferStopRequest {

    /**
     * fileToken
     */
    private String fileToken;

}
