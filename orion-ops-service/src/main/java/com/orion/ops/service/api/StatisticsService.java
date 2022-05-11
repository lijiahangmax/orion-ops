package com.orion.ops.service.api;

import com.orion.ops.entity.vo.*;

import java.util.List;

/**
 * 统计service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:13
 */
public interface StatisticsService {

    /**
     * 获取首页统计信息
     *
     * @return 统计信息
     */
    HomeStatisticsVO homeStatistics();

    /**
     * 获取调度任务统计
     *
     * @param taskId 任务id
     * @return 统计
     */
    SchedulerTaskRecordStatisticsVO schedulerTaskStatistics(Long taskId);

    /**
     * 获取应用构建统计 视图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 视图
     */
    ApplicationBuildStatisticsViewVO appBuildStatisticsView(Long appId, Long profileId);

    /**
     * 获取应用构建统计 指标
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 指标
     */
    ApplicationBuildStatisticsMetricsWrapperVO appBuildStatisticsMetrics(Long appId, Long profileId);

    /**
     * 获取应用构建统计 表格
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 折线图
     */
    List<ApplicationBuildStatisticsChartVO> appBuildStatisticsChart(Long appId, Long profileId);

    /**
     * 获取应用发布统计 视图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 视图
     */
    ApplicationReleaseStatisticsViewVO appReleaseStatisticView(Long appId, Long profileId);

    /**
     * 获取应用发布统计 指标
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 指标
     */
    ApplicationReleaseStatisticsMetricsWrapperVO appReleaseStatisticMetrics(Long appId, Long profileId);

    /**
     * 获取应用发布统计 折线图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 折线图
     */
    List<ApplicationReleaseStatisticsChartVO> appReleaseStatisticChart(Long appId, Long profileId);

    /**
     * 获取应用发布统计 视图
     *
     * @param pipelineId pipelineId
     * @return 视图
     */
    ApplicationPipelineTaskStatisticsViewVO appPipelineTaskStatisticView(Long pipelineId);

    /**
     * 获取应用发布统计 指标
     *
     * @param pipelineId pipelineId
     * @return 指标
     */
    ApplicationPipelineTaskStatisticsMetricsWrapperVO appPipelineTaskStatisticMetrics(Long pipelineId);

    /**
     * 获取应用发布统计 折线图
     *
     * @param pipelineId pipelineId
     * @return 折线图
     */
    List<ApplicationPipelineTaskStatisticsChartVO> appPipelineTaskStatisticChart(Long pipelineId);

}
