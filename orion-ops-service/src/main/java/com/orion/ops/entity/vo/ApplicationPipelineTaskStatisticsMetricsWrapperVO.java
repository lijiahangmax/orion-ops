package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 流水线执行统计指标
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:04
 */
@Data
public class ApplicationPipelineTaskStatisticsMetricsWrapperVO {

    /**
     * 最近执行指标
     */
    private ApplicationPipelineTaskStatisticsMetricsVO lately;

    /**
     * 所有执行指标
     */
    private ApplicationPipelineTaskStatisticsMetricsVO all;

}
