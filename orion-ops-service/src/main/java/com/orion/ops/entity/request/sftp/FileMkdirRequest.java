package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp mkdir请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileMkdirRequest extends FileBaseRequest {

    /**
     * 文件夹路径 相对路径
     */
    private String path;

}
