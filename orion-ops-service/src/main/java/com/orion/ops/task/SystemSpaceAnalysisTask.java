package com.orion.ops.task;

import com.orion.ops.service.api.SystemService;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统占用磁盘分析任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 13:57
 */
@Slf4j
@Component
public class SystemSpaceAnalysisTask {

    @Resource
    private SystemService systemService;

    @Scheduled(cron = "0 0 0,1,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * ?")
    private void analysisSystemSpace() {
        log.info("task-执行占用磁盘空间统计-开始 {}", Dates.current());
        // 不考虑多线程计算
        systemService.analysisSystemSpace();
        log.info("task-执行占用磁盘空间统计-结束 {}", Dates.current());
    }

}
