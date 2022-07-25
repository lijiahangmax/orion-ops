package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 系统磁盘占用分析
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 14:21
 */
@Data
public class SystemSpaceAnalysisDTO {

    /**
     * 临时文件数量
     */
    private Integer tempFileCount;

    /**
     * 临时文件大小
     */
    private String tempFileSize;

    /**
     * 日志文件数量
     */
    private Integer logFileCount;

    /**
     * 日志文件大小
     */
    private String logFileSize;

    /**
     * 交换文件数量
     */
    private Integer swapFileCount;

    /**
     * 交换文件大小
     */
    private String swapFileSize;

    /**
     * 构建产物版本数
     */
    private Integer distVersionCount;

    /**
     * 构建产物大小
     */
    private String distFileSize;

    /**
     * 应用仓库版本数
     */
    private Integer repoVersionCount;

    /**
     * 应用仓库大小
     */
    private String repoVersionFileSize;

}
