package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.vo.StatisticsVO;
import com.orion.ops.service.api.StatisticsService;
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
    public StatisticsVO homeStatistics() {
        return statisticsService.homeStatistics();
    }

}
