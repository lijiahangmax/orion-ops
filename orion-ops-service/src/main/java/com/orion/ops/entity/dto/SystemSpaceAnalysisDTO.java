package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统磁盘占用分析
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 14:21
 */
@Data
@ApiModel(value = "系统磁盘占用分析")
public class SystemSpaceAnalysisDTO {

    @ApiModelProperty(value = "临时文件数量")
    private Integer tempFileCount;

    @ApiModelProperty(value = "临时文件大小")
    private String tempFileSize;

    @ApiModelProperty(value = "日志文件数量")
    private Integer logFileCount;

    @ApiModelProperty(value = "日志文件大小")
    private String logFileSize;

    @ApiModelProperty(value = "交换文件数量")
    private Integer swapFileCount;

    @ApiModelProperty(value = "交换文件大小")
    private String swapFileSize;

    @ApiModelProperty(value = "构建产物版本数")
    private Integer distVersionCount;

    @ApiModelProperty(value = "构建产物大小")
    private String distFileSize;

    @ApiModelProperty(value = "应用仓库版本数")
    private Integer repoVersionCount;

    @ApiModelProperty(value = "应用仓库大小")
    private String repoVersionFileSize;

    @ApiModelProperty(value = "录屏文件数")
    private Integer screenFileCount;

    @ApiModelProperty(value = "录屏文件大小")
    private String screenFileSize;

}
