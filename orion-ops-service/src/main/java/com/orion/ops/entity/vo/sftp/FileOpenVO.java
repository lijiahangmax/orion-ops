package com.orion.ops.entity.vo.sftp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp open vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileOpenVO extends FileListVO {

    /**
     * 根目录
     */
    private String home;

    /**
     * sessionToken
     */
    private String sessionToken;

    /**
     * 编码格式
     */
    private String charset;

}
