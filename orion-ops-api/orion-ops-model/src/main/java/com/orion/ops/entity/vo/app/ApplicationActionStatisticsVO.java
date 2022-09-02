package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用构建统计分析操作响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:06
 */
@Data
@ApiModel(value = "应用构建统计分析操作响应")
public class ApplicationActionStatisticsVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作名称")
    private String name;

    @ApiModelProperty(value = "成功平均操作时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均操作时长")
    private String avgUsedInterval;

}
