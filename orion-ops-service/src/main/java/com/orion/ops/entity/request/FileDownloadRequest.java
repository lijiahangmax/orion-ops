package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件下载请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:08
 */
@Data
@ApiModel(value = "文件下载请求")
public class FileDownloadRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.download.FileDownloadType
     */
    @ApiModelProperty(value = "下载类型")
    private Integer type;

}
