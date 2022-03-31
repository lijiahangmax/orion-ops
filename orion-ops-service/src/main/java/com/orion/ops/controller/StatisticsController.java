package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.AppBuildStatisticsRequest;
import com.orion.ops.entity.request.SchedulerTaskStatisticsRequest;
import com.orion.ops.entity.vo.ApplicationBuildStatisticsVO;
import com.orion.ops.entity.vo.HomeStatisticsVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordStatisticsVO;
import com.orion.ops.service.api.StatisticsService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public HomeStatisticsVO homeStatistics() {
        return statisticsService.homeStatistics();
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
     * 应用构建统计
     */
    @RequestMapping("/app-build")
    public ApplicationBuildStatisticsVO appBuildStatistics(@RequestBody AppBuildStatisticsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return statisticsService.appBuildStatistics(appId, profileId);
    }

}
