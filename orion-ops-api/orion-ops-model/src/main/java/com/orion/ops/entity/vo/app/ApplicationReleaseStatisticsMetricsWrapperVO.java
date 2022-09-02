package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发布统计指标响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:17
 */
@Data
@ApiModel(value = "发布统计指标响应")
public class ApplicationReleaseStatisticsMetricsWrapperVO {

    @ApiModelProperty(value = "最近发布指标")
    private ApplicationReleaseStatisticsMetricsVO lately;

    @ApiModelProperty(value = "所有发布指标")
    private ApplicationReleaseStatisticsMetricsVO all;

}
