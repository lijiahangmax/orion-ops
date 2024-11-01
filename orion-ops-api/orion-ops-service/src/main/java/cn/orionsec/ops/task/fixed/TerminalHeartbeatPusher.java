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
package cn.orionsec.ops.task.fixed;

import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.handler.terminal.manager.TerminalSessionManager;
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
