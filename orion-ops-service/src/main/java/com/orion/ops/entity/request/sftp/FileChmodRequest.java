package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp chmod命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileChmodRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private String path;

    /**
     * 权限 10进制表现的8进制权限
     */
    private Integer permission;

}
