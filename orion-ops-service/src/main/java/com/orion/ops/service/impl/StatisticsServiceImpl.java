package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import com.orion.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import com.orion.ops.entity.dto.StatisticsCountDTO;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.StatisticsService;
import com.orion.ops.utils.Utils;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import com.orion.utils.time.Dates;
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
    private FileTailListDAO fileTailListDAO;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private ApplicationReleaseMachineDAO applicationReleaseMachineDAO;

    @Resource
    private ApplicationActionDAO applicationActionDAO;

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public HomeStatisticsVO homeStatistics() {
        HomeStatisticsVO statistics = new HomeStatisticsVO();
        // 设置数量
        HomeStatisticsCountVO count = this.homeCountStatistics();
        statistics.setCount(count);
        return statistics;
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
    public ApplicationBuildStatisticsViewVO appBuildStatisticsView(Long appId, Long profileId) {
        // 查询构建配置
        LambdaQueryWrapper<ApplicationActionDO> actionWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, profileId)
                .eq(ApplicationActionDO::getStageType, StageType.BUILD.getType());
        List<ApplicationActionDO> appActions = applicationActionDAO.selectList(actionWrapper);
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
        LambdaQueryWrapper<ApplicationActionLogDO> logWrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getStageType, StageType.BUILD.getType())
                .in(ApplicationActionLogDO::getRelId, buildIdList);
        List<ApplicationActionLogDO> buildActionLogs = applicationActionLogDAO.selectList(logWrapper);
        // 封装数据
        ApplicationBuildStatisticsViewVO analysis = new ApplicationBuildStatisticsViewVO();
        // 成功构建平均耗时
        long avgUsed = (long) buildList.stream()
                .filter(s -> BuildStatus.FINISH.getStatus().equals(s.getBuildStatus()))
                .filter(s -> s.getBuildStartTime() != null && s.getBuildEndTime() != null)
                .mapToLong(s -> s.getBuildEndTime().getTime() - s.getBuildStartTime().getTime())
                .average()
                .orElse(0);
        analysis.setAvgUsed(avgUsed);
        analysis.setAvgUsedInterval(Utils.interval(avgUsed));
        // 设置构建操作
        List<ApplicationStatisticsActionVO> statisticsActions = appActions.stream()
                .map(s -> Converts.to(s, ApplicationStatisticsActionVO.class))
                .collect(Collectors.toList());
        analysis.setActions(statisticsActions);
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
            List<ApplicationStatisticsActionLogVO> recordActionLogs = this.getStatisticsActionLogs(appActions, buildRecordActionLogs);
            buildRecord.setActionLogs(recordActionLogs);
            buildRecordList.add(buildRecord);
        }
        analysis.setBuildRecordList(buildRecordList);
        // 设置构建操作平均使用时间
        for (ApplicationStatisticsActionVO statisticsAction : statisticsActions) {
            long actionAvgUsed = (long) buildRecordList.stream()
                    .map(ApplicationBuildStatisticsRecordVO::getActionLogs)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(s -> s.getActionId().equals(statisticsAction.getId()))
                    .filter(s -> ActionStatus.FINISH.getStatus().equals(s.getStatus()))
                    .map(ApplicationStatisticsActionLogVO::getUsed)
                    .filter(Objects::nonNull)
                    .mapToLong(s -> s)
                    .average()
                    .orElse(0);
            statisticsAction.setAvgUsed(actionAvgUsed);
            statisticsAction.setAvgUsedInterval(Utils.interval(actionAvgUsed));
        }
        return analysis;
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
    public ApplicationReleaseStatisticsVO appReleaseStatistic(Long appId, Long profileId) {
        // 查询缓存
        String cacheKey = Strings.format(KeyConst.APP_RELEASE_STATISTICS_KEY, profileId, appId);
        String cacheData = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isBlank(cacheData)) {
            // 获取图表时间
            Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
            Date rangeStartDate = Arrays1.last(chartDates);
            // 获取发布统计信息
            ApplicationReleaseStatisticsDTO releaseStatisticsDTO = applicationReleaseDAO.getReleaseStatistics(appId, profileId, rangeStartDate);
            ApplicationReleaseStatisticsVO releaseStatistics = Converts.to(releaseStatisticsDTO, ApplicationReleaseStatisticsVO.class);
            // 获取构建操作分析
            ApplicationReleaseStatisticsAnalysisVO analysis = this.analysisReleaseStatistics(appId, profileId);
            releaseStatistics.setAnalysis(analysis);
            // 获取发布统计图表
            List<ApplicationReleaseStatisticsDTO> dateStatistics = applicationReleaseDAO.getReleaseDateStatistics(appId, profileId, rangeStartDate);
            Map<String, ApplicationReleaseStatisticsDTO> dateStatisticsMap = dateStatistics.stream()
                    .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
            // 填充图表数据
            List<ApplicationReleaseStatisticsChartVO> statisticsCharts = this.fillApplicationReleaseStatisticsChartData(chartDates, dateStatisticsMap);
            releaseStatistics.setCharts(statisticsCharts);
            // 设置缓存
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(releaseStatistics),
                    Integer.parseInt(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()), TimeUnit.MINUTES);
            return releaseStatistics;
        } else {
            return JSON.parseObject(cacheData, ApplicationReleaseStatisticsVO.class);
        }
    }

    /**
     * 获取首页统计数量
     *
     * @return 首页统计
     */
    private HomeStatisticsCountVO homeCountStatistics() {
        StatisticsCountDTO count;
        // 查询缓存
        String countCache = redisTemplate.opsForValue().get(KeyConst.HOME_STATISTICS_COUNT_KEY);
        if (Strings.isBlank(countCache)) {
            count = new StatisticsCountDTO();
            // 查询机器数量
            Integer machineCount = machineInfoDAO.selectCount(null);
            // 查询环境数量
            Integer profileCount = applicationProfileDAO.selectCount(null);
            // 查询应用数量
            Integer appCount = applicationInfoDAO.selectCount(null);
            // 查询日志数量
            Integer logCount = fileTailListDAO.selectCount(null);
            // 设置缓存
            count.setMachineCount(machineCount);
            count.setProfileCount(profileCount);
            count.setAppCount(appCount);
            count.setLogCount(logCount);
            redisTemplate.opsForValue().set(KeyConst.HOME_STATISTICS_COUNT_KEY, JSON.toJSONString(count),
                    Integer.parseInt(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()), TimeUnit.MINUTES);
        } else {
            count = JSON.parseObject(countCache, StatisticsCountDTO.class);
        }
        return Converts.to(count, HomeStatisticsCountVO.class);
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
     * 填充应用发布统计图表数据
     *
     * @param chartDates        图表时间
     * @param dateStatisticsMap 图表数据
     * @return data
     */
    private List<ApplicationReleaseStatisticsChartVO> fillApplicationReleaseStatisticsChartData(Date[] chartDates, Map<String, ApplicationReleaseStatisticsDTO> dateStatisticsMap) {
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

    /**
     * 获取应用发布统计分析信息
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 应用发布统计分析信息
     */
    private ApplicationReleaseStatisticsAnalysisVO analysisReleaseStatistics(Long appId, Long profileId) {
        // 查询发布配置
        LambdaQueryWrapper<ApplicationActionDO> actionWrapper = new LambdaQueryWrapper<ApplicationActionDO>()
                .eq(ApplicationActionDO::getAppId, appId)
                .eq(ApplicationActionDO::getProfileId, profileId)
                .eq(ApplicationActionDO::getStageType, StageType.RELEASE.getType());
        List<ApplicationActionDO> appActions = applicationActionDAO.selectList(actionWrapper);
        if (appActions.isEmpty()) {
            return null;
        }
        // 查询最近10次的发布记录 成功/失败/停止
        LambdaQueryWrapper<ApplicationReleaseDO> releaseWrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .eq(ApplicationReleaseDO::getAppId, appId)
                .eq(ApplicationReleaseDO::getProfileId, profileId)
                .in(ApplicationReleaseDO::getReleaseStatus, ReleaseStatus.FINISH.getStatus(), ReleaseStatus.FAILURE.getStatus(), ReleaseStatus.TERMINATED.getStatus())
                .orderByDesc(ApplicationReleaseDO::getId)
                .last("LIMIT 10");
        List<ApplicationReleaseDO> lastReleaseList = applicationReleaseDAO.selectList(releaseWrapper);
        if (lastReleaseList.isEmpty()) {
            return null;
        }
        // 查询发布机器
        List<Long> releaseIdList = lastReleaseList.stream().map(ApplicationReleaseDO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ApplicationReleaseMachineDO> releaseMachineWrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .in(ApplicationReleaseMachineDO::getReleaseId, releaseIdList);
        List<ApplicationReleaseMachineDO> releaseMachineList = applicationReleaseMachineDAO.selectList(releaseMachineWrapper);
        if (releaseMachineList.isEmpty()) {
            return null;
        }
        // 查询发布明细
        List<Long> releaseMachineIdList = releaseMachineList.stream().map(ApplicationReleaseMachineDO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ApplicationActionLogDO> logWrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getStageType, StageType.RELEASE.getType())
                .in(ApplicationActionLogDO::getRelId, releaseMachineIdList);
        List<ApplicationActionLogDO> releaseActionLogs = applicationActionLogDAO.selectList(logWrapper);
        // 封装数据
        Map<Long, List<ApplicationReleaseMachineDO>> releaseMachinesMap = releaseMachineList.stream().collect(Collectors.groupingBy(ApplicationReleaseMachineDO::getReleaseId));
        Map<Long, List<ApplicationActionLogDO>> releaseActionLogsMap = releaseActionLogs.stream().collect(Collectors.groupingBy(ApplicationActionLogDO::getRelId));
        ApplicationReleaseStatisticsAnalysisVO analysis = new ApplicationReleaseStatisticsAnalysisVO();
        // 成功发布平均耗时
        long avgUsed = (long) lastReleaseList.stream()
                .filter(s -> ReleaseStatus.FINISH.getStatus().equals(s.getReleaseStatus()))
                .filter(s -> s.getReleaseStartTime() != null && s.getReleaseEndTime() != null)
                .mapToLong(s -> s.getReleaseEndTime().getTime() - s.getReleaseStartTime().getTime())
                .average()
                .orElse(0);
        analysis.setAvgUsed(avgUsed);
        analysis.setAvgUsedInterval(Utils.interval(avgUsed));
        // 设置发布操作
        List<ApplicationStatisticsActionVO> statisticsActions = appActions.stream()
                .map(s -> Converts.to(s, ApplicationStatisticsActionVO.class))
                .collect(Collectors.toList());
        analysis.setActions(statisticsActions);
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
                List<ApplicationStatisticsActionLogVO> actionLogs = this.getStatisticsActionLogs(appActions, recordActionLogs);
                machine.setActionLogs(actionLogs);
                machines.add(machine);
            }
            record.setMachines(machines);
            recordList.add(record);
        }
        analysis.setReleaseRecordList(recordList);
        // 设置发布操作平均使用时间
        for (ApplicationStatisticsActionVO statisticsAction : statisticsActions) {
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
                    .map(ApplicationStatisticsActionLogVO::getUsed)
                    .filter(Objects::nonNull)
                    .mapToLong(s -> s)
                    .average()
                    .orElse(0);
            statisticsAction.setAvgUsed(actionAvgUsed);
            statisticsAction.setAvgUsedInterval(Utils.interval(actionAvgUsed));
        }
        return analysis;
    }

    /**
     * 获取应用统计操作日志
     *
     * @param appActions 应用操作
     * @param actionLogs 操作记录
     * @return 统计操作记录
     */
    private List<ApplicationStatisticsActionLogVO> getStatisticsActionLogs(List<ApplicationActionDO> appActions, List<ApplicationActionLogDO> actionLogs) {
        List<ApplicationStatisticsActionLogVO> recordActionLogs = Lists.newList();
        for (ApplicationActionDO appAction : appActions) {
            ApplicationStatisticsActionLogVO actionLog = actionLogs.stream()
                    .filter(s -> appAction.getActionName().equals(s.getActionName()))
                    .findFirst()
                    .map(s -> {
                        ApplicationStatisticsActionLogVO log = Converts.to(s, ApplicationStatisticsActionLogVO.class);
                        log.setActionId(appAction.getId());
                        return log;
                    })
                    .orElse(null);
            recordActionLogs.add(actionLog);
        }
        return recordActionLogs;
    }

}
