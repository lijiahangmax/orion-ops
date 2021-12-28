package com.orion.ops.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 重置应用发布状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/29 17:49
 */
@Component
@Order(150)
@Slf4j
public class CleanReleaseStatusRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("重置应用发布状态-开始");
        log.info("重置应用发布状态-结束");
    }

}
