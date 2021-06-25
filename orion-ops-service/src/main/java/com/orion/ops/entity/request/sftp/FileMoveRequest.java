package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp mv命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileMoveRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private String source;

    /**
     * 路径 绝对路径/相对路径 可以包含../
     */
    private String target;

}
