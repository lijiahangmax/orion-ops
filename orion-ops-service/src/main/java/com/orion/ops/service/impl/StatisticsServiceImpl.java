package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import com.orion.ops.entity.dto.StatisticCountDTO;
import com.orion.ops.entity.vo.SchedulerTaskRecordStatisticsChartVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordStatisticsVO;
import com.orion.ops.entity.vo.StatisticCountVO;
import com.orion.ops.entity.vo.StatisticsVO;
import com.orion.ops.service.api.StatisticsService;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
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
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public StatisticsVO homeStatistics() {
        StatisticsVO statistics = new StatisticsVO();
        // 设置数量
        StatisticCountVO count = this.homeStatisticsCount();
        statistics.setCount(count);
        return statistics;
    }

    @Override
    public SchedulerTaskRecordStatisticsVO schedulerTaskStatistic(Long taskId) {
        // 查询缓存
        String cacheKey = Strings.format(KeyConst.SCHEDULER_TASK_STATISTIC_KEY, taskId);
        String cacheData = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isBlank(cacheData)) {
            // 获取任务统计信息
            SchedulerTaskRecordStatisticsDTO taskStatisticDTO = schedulerTaskRecordDAO.getTaskRecordStatistic(taskId);
            SchedulerTaskRecordStatisticsVO statisticTask = Converts.to(taskStatisticDTO, SchedulerTaskRecordStatisticsVO.class);
            // 获取机器统计信息
            // List<SchedulerTaskRecordStatisticsDTO> machines = schedulerTaskRecordDAO.getTaskMachineRecordStatistic(taskId);
            // List<SchedulerTaskMachineRecordStatisticsVO> statisticMachines = Converts.toList(machines, SchedulerTaskMachineRecordStatisticsVO.class);
            // statisticTask.setMachineList(statisticMachines);
            // 获取图表时间
            Date[] chartDates = Dates.getIncrementDates(Dates.clearHms(), Calendar.DAY_OF_MONTH, -1, 7);
            // 获取任务统计图表
            List<SchedulerTaskRecordStatisticsDTO> dateStatistic = schedulerTaskRecordDAO.getTaskRecordDateStatistic(taskId, Arrays1.last(chartDates));
            Map<String, SchedulerTaskRecordStatisticsDTO> dateStatisticMap = dateStatistic.stream()
                    .collect(Collectors.toMap(s -> Dates.format(s.getDate(), Dates.YMD), Function.identity(), (e1, e2) -> e2));
            // 填充数据
            List<SchedulerTaskRecordStatisticsChartVO> statisticCharts = Arrays.stream(chartDates)
                    .sorted()
                    .map(s -> Dates.format(s, Dates.YMD))
                    .map(date -> Optional.ofNullable(dateStatisticMap.get(date))
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
            statisticTask.setCharts(statisticCharts);
            // 设置缓存
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(statisticTask),
                    Integer.parseInt(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()), TimeUnit.MINUTES);
            return statisticTask;
        } else {
            return JSON.parseObject(cacheData, SchedulerTaskRecordStatisticsVO.class);
        }
    }

    /**
     * 获取首页统计数量
     *
     * @return 首页统计
     */
    private StatisticCountVO homeStatisticsCount() {
        StatisticCountDTO count;
        // 查询缓存
        String countCache = redisTemplate.opsForValue().get(KeyConst.HOME_STATISTICS_COUNT_KEY);
        if (Strings.isBlank(countCache)) {
            count = new StatisticCountDTO();
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
            count = JSON.parseObject(countCache, StatisticCountDTO.class);
        }
        return Converts.to(count, StatisticCountVO.class);
    }

}
