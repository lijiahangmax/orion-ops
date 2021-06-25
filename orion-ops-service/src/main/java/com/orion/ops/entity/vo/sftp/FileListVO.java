package com.orion.ops.entity.vo.sftp;

import lombok.Data;

import java.util.List;

/**
 * sftp ls 响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:30
 */
@Data
public class FileListVO {

    /**
     * 当前路径
     */
    private String path;

    /**
     * 文件
     */
    private List<FileDetailVO> files;

}
