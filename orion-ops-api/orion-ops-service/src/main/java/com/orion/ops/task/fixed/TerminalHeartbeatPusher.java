package com.orion.ops.task.fixed;

import com.orion.ops.constant.common.EnableType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 终端 心跳主动推送服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/3/21 15:23
 */
@Slf4j
@Component
public class TerminalHeartbeatPusher {

    @Resource
    private TerminalSessionManager terminalSessionManager;

    @Scheduled(cron = "*/30 * * * * ?")
    private void checkHeartbeat() {
        EnableType type = EnableType.of(SystemEnvAttr.TERMINAL_ACTIVE_PUSH_HEARTBEAT.getValue());
        if (!type.getValue()) {
            return;
        }
        terminalSessionManager.getSessionHolder().forEach((k, v) -> {
            log.info("terminal 开始主动发送心跳 token: {}", k);
            try {
                v.sendHeartbeat();
            } catch (Exception e) {
                log.error("terminal 主动发送心跳失败 token: {} {}", k, e);
            }
        });
    }

}
