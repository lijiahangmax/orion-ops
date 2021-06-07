package com.orion.ops.handler.terminal.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 终端心跳检查服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:17
 */
@Slf4j
@Component
public class TerminalDownChecker {

    @Resource
    private TerminalSessionHolder terminalSessionHolder;

    @Scheduled(cron = "0 */1 * * * ?")
    private void configureTasks() {
        terminalSessionHolder.getSessionStore().forEach((k, v) -> {
            if (!v.isDown()) {
                return;
            }
            log.info("terminal 心跳检查down token: {}", k);
            try {
                v.heartDown();
            } catch (Exception e) {
                log.error("terminal 心跳检查断连失败 token: {} {}", k, e);
            }
        });
    }

}
