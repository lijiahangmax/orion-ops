package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 应用构建统计分析操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:02
 */
@Data
public class ApplicationBuildStatisticsViewVO {

    /**
     * 平均构建时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均构建时长 (成功)
     */
    private String avgUsedInterval;

    /**
     * 构建操作
     */
    private List<ApplicationStatisticsActionVO> actions;

    /**
     * 构建记录
     */
    private List<ApplicationBuildStatisticsRecordVO> buildRecordList;

}
