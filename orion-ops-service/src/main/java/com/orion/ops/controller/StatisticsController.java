package com.orion.ops.controller;

import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.*;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.StatisticsService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统计操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/19 15:27
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 首页统计
     */
    @RequestMapping("/home")
    public HomeStatisticsVO homeStatistics(@RequestBody HomeStatisticsRequest request) {
        return statisticsService.homeStatistics(request);
    }

    /**
     * 调度任务统计
     */
    @RequestMapping("/scheduler-task")
    public SchedulerTaskRecordStatisticsVO schedulerTaskStatistics(@RequestBody SchedulerTaskStatisticsRequest request) {
        Long taskId = Valid.notNull(request.getTaskId());
        return statisticsService.schedulerTaskStatistics(taskId);
    }

    /**
     * 应用构建统计 视图
     */
    @IgnoreLog
    @RequestMapping("/app-build/view")
    public ApplicationBuildStatisticsViewVO appBuildStatisticsView(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsView(appId, profileId);
    }

    /**
     * 应用构建统计 指标
     */
    @RequestMapping("/app-build/metrics")
    public ApplicationBuildStatisticsMetricsWrapperVO appBuildStatisticsMetrics(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsMetrics(appId, profileId);
    }

    /**
     * 应用构建统计 折线图
     */
    @RequestMapping("/app-build/chart")
    public List<ApplicationBuildStatisticsChartVO> appBuildStatisticsChart(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsChart(appId, profileId);
    }

    /**
     * 应用发布统计 视图
     */
    @IgnoreLog
    @RequestMapping("/app-release/view")
    public ApplicationReleaseStatisticsViewVO appReleaseStatisticsView(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticView(appId, profileId);
    }

    /**
     * 应用发布统计 指标
     */
    @RequestMapping("/app-release/metrics")
    public ApplicationReleaseStatisticsMetricsWrapperVO appReleaseStatisticsMetrics(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticMetrics(appId, profileId);
    }

    /**
     * 应用发布统计 折线图
     */
    @RequestMapping("/app-release/chart")
    public List<ApplicationReleaseStatisticsChartVO> appReleaseStatisticsChart(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticChart(appId, profileId);
    }

    /**
     * 应用流水线统计 视图
     */
    @IgnoreLog
    @RequestMapping("/app-pipeline/view")
    public ApplicationPipelineTaskStatisticsViewVO appReleaseStatisticsView(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticView(pipelineId);
    }

    /**
     * 应用流水线统计 指标
     */
    @RequestMapping("/app-pipeline/metrics")
    public ApplicationPipelineTaskStatisticsMetricsWrapperVO appReleaseStatisticsMetrics(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticMetrics(pipelineId);
    }

    /**
     * 应用流水线统计 折线图
     */
    @RequestMapping("/app-pipeline/chart")
    public List<ApplicationPipelineTaskStatisticsChartVO> appReleaseStatisticsChart(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticChart(pipelineId);
    }

}
