package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.dto.StatisticCountDTO;
import com.orion.ops.entity.vo.StatisticCountVO;
import com.orion.ops.entity.vo.StatisticsVO;
import com.orion.ops.service.api.StatisticsService;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public StatisticsVO homeStatistics() {
        StatisticsVO statistics = new StatisticsVO();
        // 设置数量
        StatisticCountVO count = this.statisticsCount();
        statistics.setCount(count);
        return statistics;
    }

    @Override
    public StatisticCountVO statisticsCount() {
        StatisticCountDTO count;
        // 查询缓存
        String countCache = redisTemplate.opsForValue().get(KeyConst.STATISTICS_COUNT_KEY);
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
            redisTemplate.opsForValue().set(KeyConst.STATISTICS_COUNT_KEY, JSON.toJSONString(count),
                    KeyConst.STATISTICS_COUNT_KEY_EXPIRE, TimeUnit.SECONDS);
        } else {
            count = JSON.parseObject(countCache, StatisticCountDTO.class);
        }
        return Converts.to(count, StatisticCountVO.class);
    }

}
