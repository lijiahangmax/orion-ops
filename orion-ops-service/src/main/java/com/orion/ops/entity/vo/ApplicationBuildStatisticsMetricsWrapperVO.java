package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 构建统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:03
 */
@Data
public class ApplicationBuildStatisticsMetricsWrapperVO {

    /**
     * 最近构建指标
     */
    private ApplicationBuildStatisticsMetricsVO lately;

    /**
     * 所有构建指标
     */
    private ApplicationBuildStatisticsMetricsVO all;

}
