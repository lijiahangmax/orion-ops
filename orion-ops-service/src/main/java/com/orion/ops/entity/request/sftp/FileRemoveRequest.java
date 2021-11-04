package com.orion.ops.entity.request.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sftp rm -rf 命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 23:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileRemoveRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private List<String> paths;

}
