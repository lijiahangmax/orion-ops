package com.orion.ops.controller;

import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.*;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.StatisticsService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统计操作 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/19 15:27
 */
@Api(tags = "统计操作")
@RestController
@RestWrapper
@RequestMapping("/orion/api/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @PostMapping("/home")
    @ApiOperation(value = "首页统计")
    public HomeStatisticsVO homeStatistics(@RequestBody HomeStatisticsRequest request) {
        return statisticsService.homeStatistics(request);
    }

    @PostMapping("/scheduler-task")
    @ApiOperation(value = "调度任务统计")
    public SchedulerTaskRecordStatisticsVO schedulerTaskStatistics(@RequestBody SchedulerTaskStatisticsRequest request) {
        Long taskId = Valid.notNull(request.getTaskId());
        return statisticsService.schedulerTaskStatistics(taskId);
    }

    @IgnoreLog
    @PostMapping("/app-build/view")
    @ApiOperation(value = "获取应用构建统计视图")
    public ApplicationBuildStatisticsViewVO appBuildStatisticsView(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsView(appId, profileId);
    }

    @PostMapping("/app-build/metrics")
    @ApiOperation(value = "获取应用构建统计指标")
    public ApplicationBuildStatisticsMetricsWrapperVO appBuildStatisticsMetrics(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsMetrics(appId, profileId);
    }

    @PostMapping("/app-build/chart")
    @ApiOperation(value = "获取应用构建统计折线图")
    public List<ApplicationBuildStatisticsChartVO> appBuildStatisticsChart(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatisticsChart(appId, profileId);
    }

    @IgnoreLog
    @PostMapping("/app-release/view")
    @ApiOperation(value = "获取应用发布统计视图")
    public ApplicationReleaseStatisticsViewVO appReleaseStatisticsView(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticView(appId, profileId);
    }

    @PostMapping("/app-release/metrics")
    @ApiOperation(value = "获取应用发布统计指标")
    public ApplicationReleaseStatisticsMetricsWrapperVO appReleaseStatisticsMetrics(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticMetrics(appId, profileId);
    }

    @PostMapping("/app-release/chart")
    @ApiOperation(value = "获取应用发布统计折线图")
    public List<ApplicationReleaseStatisticsChartVO> appReleaseStatisticsChart(@RequestBody AppReleaseStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appReleaseStatisticChart(appId, profileId);
    }

    @IgnoreLog
    @PostMapping("/app-pipeline/view")
    @ApiOperation(value = "获取应用流水线统计视图")
    public ApplicationPipelineTaskStatisticsViewVO appReleaseStatisticsView(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticView(pipelineId);
    }

    @PostMapping("/app-pipeline/metrics")
    @ApiOperation(value = "获取应用流水线统计指标")
    public ApplicationPipelineTaskStatisticsMetricsWrapperVO appReleaseStatisticsMetrics(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticMetrics(pipelineId);
    }

    @PostMapping("/app-pipeline/chart")
    @ApiOperation(value = "获取应用流水线统计折线图")
    public List<ApplicationPipelineTaskStatisticsChartVO> appReleaseStatisticsChart(@RequestBody AppPipelineTaskStatisticsRequest request) {
        Long pipelineId = Valid.notNull(request.getPipelineId());
        return statisticsService.appPipelineTaskStatisticChart(pipelineId);
    }

}
