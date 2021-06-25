package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp truncate 请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/24 19:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTruncateRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private String path;

}
