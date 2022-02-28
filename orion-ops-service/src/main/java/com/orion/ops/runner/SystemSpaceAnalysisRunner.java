package com.orion.ops.runner;

import com.orion.ops.service.api.SystemService;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统占用磁盘分析任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 15:18
 */
@Slf4j
@Component
@Order(3200)
public class SystemSpaceAnalysisRunner implements CommandLineRunner {

    @Resource
    private SystemService systemService;

    @Override
    public void run(String... args) {
        try {
            log.info("runner-执行占用磁盘空间统计-开始 {}", Dates.current());
            // 不考虑多线程计算
            systemService.analysisSystemSpace();
            log.info("runner-执行占用磁盘空间统计-结束 {}", Dates.current());
        } catch (Exception e) {
            log.error("runner-执行占用磁盘空间统计-失败 {}", Dates.current(), e);
        }
    }

}
