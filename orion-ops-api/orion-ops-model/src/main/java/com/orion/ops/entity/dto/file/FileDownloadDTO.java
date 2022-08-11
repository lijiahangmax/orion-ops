package com.orion.ops.entity.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件下载缓存对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:54
 */
@Data
@ApiModel(value = "文件下载缓存对象")
@SuppressWarnings("ALL")
public class FileDownloadDTO {

    @ApiModelProperty(value = "文件绝对路径")
    private String filePath;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "下载用户id")
    private Long userId;

    /**
     * @see com.orion.ops.constant.download.FileDownloadType
     */
    @ApiModelProperty(value = "下载类型")
    private Integer type;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

}
