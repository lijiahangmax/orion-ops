package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 构建统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:03
 */
@Data
@ApiModel(value = "构建统计响应")
public class ApplicationBuildStatisticsMetricsWrapperVO {

    @ApiModelProperty(value = "最近构建指标")
    private ApplicationBuildStatisticsMetricsVO lately;

    @ApiModelProperty(value = "所有构建指标")
    private ApplicationBuildStatisticsMetricsVO all;

}
