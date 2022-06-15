package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流水线执行统计指标响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:04
 */
@Data
@ApiModel(value = "流水线执行统计指标响应")
public class ApplicationPipelineTaskStatisticsMetricsWrapperVO {

    @ApiModelProperty(value = "最近执行指标")
    private ApplicationPipelineTaskStatisticsMetricsVO lately;

    @ApiModelProperty(value = "所有执行指标")
    private ApplicationPipelineTaskStatisticsMetricsVO all;

}
