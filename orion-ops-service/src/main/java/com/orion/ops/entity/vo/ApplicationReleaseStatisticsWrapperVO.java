package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 发布统计指标
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:17
 */
@Data
public class ApplicationReleaseStatisticsWrapperVO {

    /**
     * 最近发布指标
     */
    private ApplicationReleaseStatisticsMetricsVO lately;

    /**
     * 所有发布指标
     */
    private ApplicationReleaseStatisticsMetricsVO all;

}
