package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量上传请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:42
 */
@Data
@ApiModel(value = "批量上传请求")
public class BatchUploadRequest {

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "远程路径")
    private String remotePath;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIds;

    @ApiModelProperty(value = "文件名称")
    private List<String> names;

}
