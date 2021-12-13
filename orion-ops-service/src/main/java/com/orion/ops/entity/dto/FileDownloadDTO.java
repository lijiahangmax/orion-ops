package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 文件下载缓存对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:54
 */
@Data
public class FileDownloadDTO {

    /**
     * 文件绝对路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 下载用户id
     */
    private Long userId;

    /**
     * type
     *
     * @see com.orion.ops.consts.download.FileDownloadType
     */
    private Integer type;

    /**
     * 机器id
     */
    private Long machineId;

}
