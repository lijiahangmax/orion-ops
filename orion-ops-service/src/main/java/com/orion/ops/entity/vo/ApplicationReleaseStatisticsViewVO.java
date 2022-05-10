package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 应用发布统计分析
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 18:05
 */
@Data
public class ApplicationReleaseStatisticsViewVO {

    /**
     * 平均发布时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均发布时长 (成功)
     */
    private String avgUsedInterval;

    /**
     * 发布明细列表
     */
    private List<ApplicationReleaseStatisticsRecordVO> releaseRecordList;

    /**
     * 发布操作
     */
    private List<ApplicationStatisticsActionVO> actions;

}
