package com.orion.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统分析响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 22:25
 */
@Data
@ApiModel(value = "系统分析响应")
public class SystemAnalysisVO {

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

    @ApiModelProperty(value = "文件清理阈值")
    private Integer fileCleanThreshold;

    @ApiModelProperty(value = "自动清理文件")
    private Boolean autoCleanFile;

}
