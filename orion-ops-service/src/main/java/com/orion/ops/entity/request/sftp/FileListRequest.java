package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp ll 请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileListRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private String path;

}
