/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.app.*;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.*;
import com.orion.ops.entity.dto.statistic.StatisticsCountDTO;
import com.orion.ops.entity.request.home.HomeStatisticsRequest;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.entity.vo.home.HomeStatisticsCountVO;
import com.orion.ops.entity.vo.home.HomeStatisticsVO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskRecordStatisticsChartVO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskRecordStatisticsVO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Utils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:13
 */
@Service("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationPipelineDAO applicationPipelineDAO;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private ApplicationReleaseMachineService applicationReleaseMachineService;

    @Resource
    private ApplicationActionService applicationActionService;

    @Resource
    private ApplicationActionLogService applicationActionLogService;

    @Resource
    private ApplicationPipelineDetailService applicationPipelineDetailService;

    @Resource
    private ApplicationPipelineTaskDAO applicationPipelineTaskDAO;

    @Resource
    private ApplicationPipelineTaskDetailService applicationPipelineTaskDetailService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HomeStatisticsVO homeStatistics(HomeStatisticsRequest request) {
        HomeStatisticsVO statistics = new HomeStatisticsVO();
        // 设置数量
        HomeStatisticsCountVO count = this.homeCountStatistics(request.getProfileId());
        statistics.setCount(count);
        return statistics;
    }

    /**
     * 获取首页统计数量
     *
     * @param profileId profileId
     * @return 首页统计
     */
    private HomeStatisticsCountVO homeCountStatistics(Long profileId) {
        StatisticsCountDTO count;
        // 查询缓存
        String key = Strings.format(KeyConst.HOME_STATISTICS_COUNT_KEY, profileId);
        String countCache = redisTemplate.opsForValue().get(key);
        if (Strings.isBlank(countCache)) {
            count = new StatisticsCountDTO();
            // 查询机器数量
            Integer machineCount = machineInfoDAO.selectCount(null);
            // 查询环境数量
            Integer profileCount = applicationProfileDAO.selectCount(null);
            // 查询应用数量
            Integer appCount = applicationInfoDAO.selectCount(null);
            // 查询流水线数量
            LambdaQueryWrapper<ApplicationPipelineDO> pipelineWrapper = new LambdaQueryWrapper<ApplicationPipelineDO>()
                    .eq(ApplicationPipelineDO::getProfileId, profileId);
            Integer pipelineCount = applicationPipelineDAO.selectCount(pipelineWrapper);
            // 设置缓存
            count.setMachineCount(machineCount);
            count.setProfileCount(profileCount);
            count.setAppCount(appCount);
            count.setPipelineCount(pipelineCount);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(count),
                    Integer.parseInt(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()), TimeUnit.MINUTES);
        } else {
            count = JSON.parseObject(countCache, StatisticsCountDTO.class);
        }
        return Converts.to(count, HomeStatisticsCountVO.class);
    }

    @Override
    public SchedulerTaskRecordStatisticsVO schedulerTaskStatistics(Long taskId) {
        // 查询缓存
        String cacheKey = Strings.format(KeyConst.SCHEDULER_TASK_STATISTICS_KEY, taskId);
        String cacheData = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isBlank(cacheData)) {
            // 获取图表时间
            Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
            Date rangeStartDate = Arrays1.last(chartDates);
            // 获取任务统计信息
            SchedulerTaskRecordStatisticsDTO taskStatisticsDTO = schedulerTaskRecordDAO.getTaskRecordStatistics(taskId, rangeStartDate);
            SchedulerTaskRecordStatisticsVO statisticsTask = Converts.to(taskStatisticsDTO, SchedulerTaskRecordStatisticsVO.class);
            // 获取机器统计信息
            // List<SchedulerTaskRecordStatisticsDTO> machines = schedulerTaskRecordDAO.getTaskMachineRecordStatistics(taskId);
            // List<SchedulerTaskMachineRecordStatisticsVO> statisticsMachines = Converts.toList(machines, SchedulerTaskMachineRecordStatisticsVO.class);
            // statisticsTask.setMachineList(statisticsMachines);
            // 获取任务统计图表
            List<SchedulerTaskRecordStatisticsDTO> dateStatistics = schedulerTaskRecordDAO.getTaskRecordDateStatistics(taskId, rangeStartDate);
            Map<String, SchedulerTaskRecordStatisticsDTO> dateStatisticsMap = dateStatistics.stream()
                    .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
            // 填充图表数据
            List<SchedulerTaskRecordStatisticsChartVO> statisticsCharts = this.fillSchedulerStatisticsChartData(chartDates, dateStatisticsMap);
            statisticsTask.setCharts(statisticsCharts);
            // 设置缓存
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(statisticsTask),
                    Integer.parseInt(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()), TimeUnit.MINUTES);
            return statisticsTask;
        } else {
            return JSON.parseObject(cacheData, SchedulerTaskRecordStatisticsVO.class);
        }
    }

    @Override
    public ApplicationBuildStatisticsViewVO appBuildStatisticsView(Long appId, Long profileId) {
        // 查询构建配置
        List<ApplicationActionDO> appActions = applicationActionService.getAppProfileActions(appId, profileId, StageType.BUILD.getType());
        if (appActions.isEmpty()) {
            return null;
        }
        // 查询最近10次的构建记录 进行中/成功/失败/停止
        LambdaQueryWrapper<ApplicationBuildDO> buildWrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .eq(ApplicationBuildDO::getAppId, appId)
                .eq(ApplicationBuildDO::getProfileId, profileId)
                .in(ApplicationBuildDO::getBuildStatus,
                        BuildStatus.RUNNABLE.getStatus(),
                        BuildStatus.FINISH.getStatus(),
                        BuildStatus.FAILURE.getStatus(),
                        BuildStatus.TERMINATED.getStatus())
                .orderByDesc(ApplicationBuildDO::getId)
                .last("LIMIT 10");
        List<ApplicationBuildDO> buildList = applicationBuildDAO.selectList(buildWrapper);
        if (buildList.isEmpty()) {
            return null;
        }
        // 查询构建明细
        List<Long> buildIdList = buildList.stream().map(ApplicationBuildDO::getId).collect(Collectors.toList());
        List<ApplicationActionLogDO> buildActionLogs = applicationActionLogService.selectActionByRelIdList(buildIdList, StageType.BUILD);
        // 封装数据
        ApplicationBuildStatisticsViewVO view = new ApplicationBuildStatisticsViewVO();
        // 成功构建平均耗时
        long avgUsed = (long) buildList.stream()
                .filter(s -> BuildStatus.FINISH.getStatus().equals(s.getBuildStatus()))
                .filter(s -> s.getBuildStartTime() != null && s.getBuildEndTime() != null)
                .mapToLong(s -> s.getBuildEndTime().getTime() - s.getBuildStartTime().getTime())
                .average()
                .orElse(0);
        view.setAvgUsed(avgUsed);
        view.setAvgUsedInterval(Utils.interval(avgUsed));
        // 设置构建操作
        List<ApplicationActionStatisticsVO> statisticsActions = Converts.toList(appActions, ApplicationActionStatisticsVO.class);
        view.setActions(statisticsActions);
        // 设置构建操作日志
        Map<Long, List<ApplicationActionLogDO>> buildActionLogsMap = buildActionLogs.stream().collect(Collectors.groupingBy(ApplicationActionLogDO::getRelId));
        List<ApplicationBuildStatisticsRecordVO> buildRecordList = Lists.newList();
        for (ApplicationBuildDO build : buildList) {
            // 设置构建信息
            ApplicationBuildStatisticsRecordVO buildRecord = Converts.to(build, ApplicationBuildStatisticsRecordVO.class);
            // 设置构建操作配置
            List<ApplicationActionLogDO> buildRecordActionLogs = buildActionLogsMap.get(build.getId());
            if (Lists.isEmpty(buildRecordActionLogs)) {
                continue;
            }
            // 设置构建操作日志
            List<ApplicationActionLogStatisticsVO> recordActionLogs = this.getStatisticsActionLogs(appActions, buildRecordActionLogs);
            buildRecord.setActionLogs(recordActionLogs);
            buildRecordList.add(buildRecord);
        }
        view.setBuildRecordList(buildRecordList);
        // 设置构建操作平均使用时间
        for (ApplicationActionStatisticsVO statisticsAction : statisticsActions) {
            long actionAvgUsed = (long) buildRecordList.stream()
                    .map(ApplicationBuildStatisticsRecordVO::getActionLogs)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(s -> s.getActionId().equals(statisticsAction.getId()))
                    .filter(s -> ActionStatus.FINISH.getStatus().equals(s.getStatus()))
                    .map(ApplicationActionLogStatisticsVO::getUsed)
                    .filter(Objects::nonNull)
                    .mapToLong(s -> s)
                    .average()
                    .orElse(0);
            statisticsAction.setAvgUsed(actionAvgUsed);
            statisticsAction.setAvgUsedInterval(Utils.interval(actionAvgUsed));
        }
        return view;
    }

    @Override
    public ApplicationBuildStatisticsMetricsWrapperVO appBuildStatisticsMetrics(Long appId, Long profileId) {
        ApplicationBuildStatisticsMetricsWrapperVO wrapper = new ApplicationBuildStatisticsMetricsWrapperVO();
        // 获取图表时间
        Date rangeStartDate = Dates.stream().addDay(-7).get();
        // 获取最近构建统计信息
        ApplicationBuildStatisticsDTO lately = applicationBuildDAO.getBuildStatistics(appId, profileId, rangeStartDate);
        wrapper.setLately(Converts.to(lately, ApplicationBuildStatisticsMetricsVO.class));
        // 获取所有构建统计信息
        ApplicationBuildStatisticsDTO all = applicationBuildDAO.getBuildStatistics(appId, profileId, null);
        wrapper.setAll(Converts.to(all, ApplicationBuildStatisticsMetricsVO.class));
        return wrapper;
    }

    @Override
    public List<ApplicationBuildStatisticsChartVO> appBuildStatisticsChart(Long appId, Long profileId) {
        // 获取图表时间
        Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
        Date rangeStartDate = Arrays1.last(chartDates);
        // 获取构建统计图表
        List<ApplicationBuildStatisticsDTO> dateStatistics = applicationBuildDAO.getBuildDateStatistics(appId, profileId, rangeStartDate);
        Map<String, ApplicationBuildStatisticsDTO> dateStatisticsMap = dateStatistics.stream()
                .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
        // 填充图表数据
        return Arrays.stream(chartDates)
                .sorted()
                .map(s -> Dates.format(s, Dates.YMD))
                .map(date -> Optional.ofNullable(dateStatisticsMap.get(date))
                        .map(s -> Converts.to(s, ApplicationBuildStatisticsChartVO.class))
                        .orElseGet(() -> {
                            ApplicationBuildStatisticsChartVO dateChart = new ApplicationBuildStatisticsChartVO();
                            dateChart.setDate(date);
                            dateChart.setBuildCount(0);
                            dateChart.setSuccessCount(0);
                            dateChart.setFailureCount(0);
                            return dateChart;
                        }))
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationReleaseStatisticsViewVO appReleaseStatisticView(Long appId, Long profileId) {
        // 查询发布配置
        List<ApplicationActionDO> appActions = applicationActionService.getAppProfileActions(appId, profileId, StageType.RELEASE.getType());
        if (appActions.isEmpty()) {
            return null;
        }
        // 查询最近10次的发布记录 进行中/成功/失败/停止
        LambdaQueryWrapper<ApplicationReleaseDO> releaseWrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .eq(ApplicationReleaseDO::getAppId, appId)
                .eq(ApplicationReleaseDO::getProfileId, profileId)
                .in(ApplicationReleaseDO::getReleaseStatus,
                        ReleaseStatus.RUNNABLE.getStatus(),
                        ReleaseStatus.FINISH.getStatus(),
                        ReleaseStatus.FAILURE.getStatus(),
                        ReleaseStatus.TERMINATED.getStatus())
                .orderByDesc(ApplicationReleaseDO::getId)
                .last("LIMIT 10");
        List<ApplicationReleaseDO> lastReleaseList = applicationReleaseDAO.selectList(releaseWrapper);
        if (lastReleaseList.isEmpty()) {
            return null;
        }
        // 查询发布机器
        List<Long> releaseIdList = lastReleaseList.stream().map(ApplicationReleaseDO::getId).collect(Collectors.toList());
        List<ApplicationReleaseMachineDO> releaseMachineList = applicationReleaseMachineService.getReleaseMachines(releaseIdList);
        if (releaseMachineList.isEmpty()) {
            return null;
        }
        // 查询发布明细
        List<Long> releaseMachineIdList = releaseMachineList.stream().map(ApplicationReleaseMachineDO::getId).collect(Collectors.toList());
        List<ApplicationActionLogDO> releaseActionLogs = applicationActionLogService.selectActionByRelIdList(releaseMachineIdList, StageType.RELEASE);
        // 封装数据
        Map<Long, List<ApplicationReleaseMachineDO>> releaseMachinesMap = releaseMachineList.stream().collect(Collectors.groupingBy(ApplicationReleaseMachineDO::getReleaseId));
        Map<Long, List<ApplicationActionLogDO>> releaseActionLogsMap = releaseActionLogs.stream().collect(Collectors.groupingBy(ApplicationActionLogDO::getRelId));
        ApplicationReleaseStatisticsViewVO view = new ApplicationReleaseStatisticsViewVO();
        // 成功发布平均耗时
        long avgUsed = (long) lastReleaseList.stream()
                .filter(s -> ReleaseStatus.FINISH.getStatus().equals(s.getReleaseStatus()))
                .filter(s -> s.getReleaseStartTime() != null && s.getReleaseEndTime() != null)
                .mapToLong(s -> s.getReleaseEndTime().getTime() - s.getReleaseStartTime().getTime())
                .average()
                .orElse(0);
        view.setAvgUsed(avgUsed);
        view.setAvgUsedInterval(Utils.interval(avgUsed));
        // 设置发布操作
        List<ApplicationActionStatisticsVO> statisticsActions = Converts.toList(appActions, ApplicationActionStatisticsVO.class);
        view.setActions(statisticsActions);
        // 设置发布操作日志
        List<ApplicationReleaseStatisticsRecordVO> recordList = Lists.newList();
        for (ApplicationReleaseDO release : lastReleaseList) {
            // 封装发布记录
            ApplicationReleaseStatisticsRecordVO record = Converts.to(release, ApplicationReleaseStatisticsRecordVO.class);
            List<ApplicationReleaseMachineDO> releaseMachines = releaseMachinesMap.get(release.getId());
            if (Lists.isEmpty(releaseMachines)) {
                continue;
            }
            // 封装发布机器
            List<ApplicationReleaseStatisticsMachineVO> machines = Lists.newList();
            for (ApplicationReleaseMachineDO releaseMachine : releaseMachines) {
                ApplicationReleaseStatisticsMachineVO machine = Converts.to(releaseMachine, ApplicationReleaseStatisticsMachineVO.class);
                // 封装发布操作
                List<ApplicationActionLogDO> recordActionLogs = releaseActionLogsMap.get(releaseMachine.getId());
                if (Lists.isEmpty(recordActionLogs)) {
                    continue;
                }
                List<ApplicationActionLogStatisticsVO> actionLogs = this.getStatisticsActionLogs(appActions, recordActionLogs);
                machine.setActionLogs(actionLogs);
                machines.add(machine);
            }
            record.setMachines(machines);
            recordList.add(record);
        }
        view.setReleaseRecordList(recordList);
        // 设置发布操作平均使用时间
        for (ApplicationActionStatisticsVO statisticsAction : statisticsActions) {
            long actionAvgUsed = (long) recordList.stream()
                    .map(ApplicationReleaseStatisticsRecordVO::getMachines)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(ApplicationReleaseStatisticsMachineVO::getActionLogs)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(s -> s.getActionId().equals(statisticsAction.getId()))
                    .filter(s -> ActionStatus.FINISH.getStatus().equals(s.getStatus()))
                    .map(ApplicationActionLogStatisticsVO::getUsed)
                    .filter(Objects::nonNull)
                    .mapToLong(s -> s)
                    .average()
                    .orElse(0);
            statisticsAction.setAvgUsed(actionAvgUsed);
            statisticsAction.setAvgUsedInterval(Utils.interval(actionAvgUsed));
        }
        return view;
    }

    @Override
    public ApplicationReleaseStatisticsMetricsWrapperVO appReleaseStatisticMetrics(Long appId, Long profileId) {
        ApplicationReleaseStatisticsMetricsWrapperVO wrapper = new ApplicationReleaseStatisticsMetricsWrapperVO();
        // 获取图表时间
        Date rangeStartDate = Dates.stream().addDay(-7).get();
        // 获取最近发布统计信息
        ApplicationReleaseStatisticsDTO lately = applicationReleaseDAO.getReleaseStatistics(appId, profileId, rangeStartDate);
        wrapper.setLately(Converts.to(lately, ApplicationReleaseStatisticsMetricsVO.class));
        // 获取所有发布统计信息
        ApplicationReleaseStatisticsDTO all = applicationReleaseDAO.getReleaseStatistics(appId, profileId, null);
        wrapper.setAll(Converts.to(all, ApplicationReleaseStatisticsMetricsVO.class));
        return wrapper;
    }

    @Override
    public List<ApplicationReleaseStatisticsChartVO> appReleaseStatisticChart(Long appId, Long profileId) {
        Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
        Date rangeStartDate = Arrays1.last(chartDates);
        // 获取发布统计图表
        List<ApplicationReleaseStatisticsDTO> dateStatistics = applicationReleaseDAO.getReleaseDateStatistics(appId, profileId, rangeStartDate);
        Map<String, ApplicationReleaseStatisticsDTO> dateStatisticsMap = dateStatistics.stream()
                .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
        return Arrays.stream(chartDates)
                .sorted()
                .map(s -> Dates.format(s, Dates.YMD))
                .map(date -> Optional.ofNullable(dateStatisticsMap.get(date))
                        .map(s -> Converts.to(s, ApplicationReleaseStatisticsChartVO.class))
                        .orElseGet(() -> {
                            ApplicationReleaseStatisticsChartVO dateChart = new ApplicationReleaseStatisticsChartVO();
                            dateChart.setDate(date);
                            dateChart.setReleaseCount(0);
                            dateChart.setSuccessCount(0);
                            dateChart.setFailureCount(0);
                            return dateChart;
                        }))
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationPipelineTaskStatisticsViewVO appPipelineTaskStatisticView(Long pipelineId) {
        // 查询流水线配置
        List<ApplicationPipelineDetailDO> pipelineDetails = applicationPipelineDetailService.selectByPipelineId(pipelineId);
        if (pipelineDetails.isEmpty()) {
            return null;
        }
        // 查询最近10次的执行记录 进行中/成功/失败/停止
        LambdaQueryWrapper<ApplicationPipelineTaskDO> pipelineTaskWrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDO>()
                .eq(ApplicationPipelineTaskDO::getPipelineId, pipelineId)
                .in(ApplicationPipelineTaskDO::getExecStatus,
                        PipelineStatus.RUNNABLE.getStatus(),
                        PipelineStatus.FINISH.getStatus(),
                        PipelineStatus.FAILURE.getStatus(),
                        PipelineStatus.TERMINATED.getStatus())
                .orderByDesc(ApplicationPipelineTaskDO::getId)
                .last("LIMIT 10");
        List<ApplicationPipelineTaskDO> taskList = applicationPipelineTaskDAO.selectList(pipelineTaskWrapper);
        if (taskList.isEmpty()) {
            return null;
        }
        // 查询执行任务
        List<Long> pipelineTaskIdList = taskList.stream().map(ApplicationPipelineTaskDO::getId).collect(Collectors.toList());
        List<ApplicationPipelineTaskDetailDO> pipelineTaskDetails = applicationPipelineTaskDetailService.selectTaskDetails(pipelineTaskIdList);
        // 封装数据
        ApplicationPipelineTaskStatisticsViewVO view = new ApplicationPipelineTaskStatisticsViewVO();
        // 成功执行平均耗时
        long avgUsed = (long) taskList.stream()
                .filter(s -> PipelineStatus.FINISH.getStatus().equals(s.getExecStatus()))
                .filter(s -> s.getExecStartTime() != null && s.getExecEndTime() != null)
                .mapToLong(s -> s.getExecEndTime().getTime() - s.getExecStartTime().getTime())
                .average()
                .orElse(0);
        view.setAvgUsed(avgUsed);
        view.setAvgUsedInterval(Utils.interval(avgUsed));
        // 设置流水线配置
        List<ApplicationPipelineStatisticsDetailVO> details = Converts.toList(pipelineDetails, ApplicationPipelineStatisticsDetailVO.class);
        view.setDetails(details);
        // 查询应用名称
        List<Long> appIdList = details.stream().map(ApplicationPipelineStatisticsDetailVO::getAppId).collect(Collectors.toList());
        List<ApplicationInfoDO> appNameList = applicationInfoDAO.selectNameByIdList(appIdList);
        details.forEach(d -> {
            appNameList.stream().filter(s -> s.getId().equals(d.getAppId()))
                    .findFirst()
                    .map(ApplicationInfoDO::getAppName)
                    .ifPresent(d::setAppName);
        });
        // 设置流水线任务信息
        Map<Long, List<ApplicationPipelineTaskDetailDO>> taskDetailsGroup = pipelineTaskDetails.stream().collect(Collectors.groupingBy(ApplicationPipelineTaskDetailDO::getTaskId));
        List<ApplicationPipelineTaskStatisticsTaskVO> pipelineTaskList = Lists.newList();
        for (ApplicationPipelineTaskDO task : taskList) {
            // 设置流水线信息
            ApplicationPipelineTaskStatisticsTaskVO statisticsTask = Converts.to(task, ApplicationPipelineTaskStatisticsTaskVO.class);
            // 设置流水线操作
            List<ApplicationPipelineTaskDetailDO> taskDetails = taskDetailsGroup.get(task.getId());
            if (Lists.isEmpty(taskDetails)) {
                continue;
            }
            // 设置流水线操作日志
            List<ApplicationPipelineTaskStatisticsDetailVO> taskDetailLogList = this.getStatisticsPipelineDetailLogs(pipelineDetails, taskDetails);
            statisticsTask.setDetails(taskDetailLogList);
            pipelineTaskList.add(statisticsTask);
        }
        view.setPipelineTaskList(pipelineTaskList);
        // 设置流水线操作平均使用时间
        for (ApplicationPipelineStatisticsDetailVO detail : details) {
            long actionAvgUsed = (long) pipelineTaskList.stream()
                    .map(ApplicationPipelineTaskStatisticsTaskVO::getDetails)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(s -> s.getDetailId().equals(detail.getId()))
                    .filter(s -> PipelineDetailStatus.FINISH.getStatus().equals(s.getStatus()))
                    .map(ApplicationPipelineTaskStatisticsDetailVO::getUsed)
                    .filter(Objects::nonNull)
                    .mapToLong(s -> s)
                    .average()
                    .orElse(0);
            detail.setAvgUsed(actionAvgUsed);
            detail.setAvgUsedInterval(Utils.interval(actionAvgUsed));
        }
        return view;
    }

    @Override
    public ApplicationPipelineTaskStatisticsMetricsWrapperVO appPipelineTaskStatisticMetrics(Long pipelineId) {
        ApplicationPipelineTaskStatisticsMetricsWrapperVO wrapper = new ApplicationPipelineTaskStatisticsMetricsWrapperVO();
        // 获取图表时间
        Date rangeStartDate = Dates.stream().addDay(-7).get();
        // 获取最近发布统计信息
        ApplicationPipelineTaskStatisticsDTO lately = applicationPipelineTaskDAO.getPipelineTaskStatistics(pipelineId, rangeStartDate);
        wrapper.setLately(Converts.to(lately, ApplicationPipelineTaskStatisticsMetricsVO.class));
        // 获取所有发布统计信息
        ApplicationPipelineTaskStatisticsDTO all = applicationPipelineTaskDAO.getPipelineTaskStatistics(pipelineId, null);
        wrapper.setAll(Converts.to(all, ApplicationPipelineTaskStatisticsMetricsVO.class));
        return wrapper;
    }

    @Override
    public List<ApplicationPipelineTaskStatisticsChartVO> appPipelineTaskStatisticChart(Long pipelineId) {
        Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
        Date rangeStartDate = Arrays1.last(chartDates);
        // 获取发布统计图表
        List<ApplicationPipelineTaskStatisticsDTO> dateStatistics = applicationPipelineTaskDAO.getPipelineTaskDateStatistics(pipelineId, rangeStartDate);
        Map<String, ApplicationPipelineTaskStatisticsDTO> dateStatisticsMap = dateStatistics.stream()
                .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
        return Arrays.stream(chartDates)
                .sorted()
                .map(s -> Dates.format(s, Dates.YMD))
                .map(date -> Optional.ofNullable(dateStatisticsMap.get(date))
                        .map(s -> Converts.to(s, ApplicationPipelineTaskStatisticsChartVO.class))
                        .orElseGet(() -> {
                            ApplicationPipelineTaskStatisticsChartVO dateChart = new ApplicationPipelineTaskStatisticsChartVO();
                            dateChart.setDate(date);
                            dateChart.setExecCount(0);
                            dateChart.setSuccessCount(0);
                            dateChart.setFailureCount(0);
                            return dateChart;
                        }))
                .collect(Collectors.toList());
    }

    /**
     * 填充调度任务统计图表数据
     *
     * @param chartDates        图表时间
     * @param dateStatisticsMap 图表数据
     * @return data
     */
    private List<SchedulerTaskRecordStatisticsChartVO> fillSchedulerStatisticsChartData(Date[] chartDates, Map<String, SchedulerTaskRecordStatisticsDTO> dateStatisticsMap) {
        return Arrays.stream(chartDates)
                .sorted()
                .map(s -> Dates.format(s, Dates.YMD))
                .map(date -> Optional.ofNullable(dateStatisticsMap.get(date))
                        .map(s -> Converts.to(s, SchedulerTaskRecordStatisticsChartVO.class))
                        .orElseGet(() -> {
                            SchedulerTaskRecordStatisticsChartVO dateChart = new SchedulerTaskRecordStatisticsChartVO();
                            dateChart.setDate(date);
                            dateChart.setScheduledCount(0);
                            dateChart.setSuccessCount(0);
                            dateChart.setFailureCount(0);
                            return dateChart;
                        }))
                .collect(Collectors.toList());
    }

    /**
     * 获取应用统计操作日志
     *
     * @param appActions 应用操作
     * @param actionLogs 操作记录
     * @return 统计操作记录
     */
    private List<ApplicationActionLogStatisticsVO> getStatisticsActionLogs(List<ApplicationActionDO> appActions, List<ApplicationActionLogDO> actionLogs) {
        List<ApplicationActionLogStatisticsVO> recordActionLogs = Lists.newList();
        for (ApplicationActionDO appAction : appActions) {
            ApplicationActionLogStatisticsVO actionLog = actionLogs.stream()
                    .filter(s -> appAction.getActionName().equals(s.getActionName()))
                    .findFirst()
                    .map(s -> {
                        ApplicationActionLogStatisticsVO log = Converts.to(s, ApplicationActionLogStatisticsVO.class);
                        log.setActionId(appAction.getId());
                        return log;
                    })
                    .orElse(null);
            recordActionLogs.add(actionLog);
        }
        return recordActionLogs;
    }

    /**
     * 获取应用流水线明细执行日志
     *
     * @param pipelineDetails pipelineDetails
     * @param taskDetails     taskDetails
     * @return 执行明细
     */
    private List<ApplicationPipelineTaskStatisticsDetailVO> getStatisticsPipelineDetailLogs(List<ApplicationPipelineDetailDO> pipelineDetails, List<ApplicationPipelineTaskDetailDO> taskDetails) {
        List<ApplicationPipelineTaskStatisticsDetailVO> detailLogs = Lists.newList();
        for (ApplicationPipelineDetailDO pipelineDetail : pipelineDetails) {
            ApplicationPipelineTaskStatisticsDetailVO detailLog = taskDetails.stream()
                    .filter(s -> s.getAppId().equals(pipelineDetail.getAppId()))
                    .filter(s -> s.getStageType().equals(pipelineDetail.getStageType()))
                    .findFirst()
                    .map(s -> {
                        ApplicationPipelineTaskStatisticsDetailVO log = Converts.to(s, ApplicationPipelineTaskStatisticsDetailVO.class);
                        log.setDetailId(pipelineDetail.getId());
                        return log;
                    })
                    .orElse(null);
            detailLogs.add(detailLog);
        }
        return detailLogs;
    }

}
