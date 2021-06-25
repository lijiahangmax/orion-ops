package com.orion.ops.entity.request.sftp;

import lombok.Data;

/**
 * sftp 打开请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:50
 */
@Data
public class FileOpenRequest {

    /**
     * 机器id
     */
    private Long machineId;

}
