/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.ops.handler.terminal.manager.TerminalSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 终端 心跳检查服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:17
 */
@Slf4j
@Component
public class TerminalHeartbeatDownChecker {

    @Resource
    private TerminalSessionManager terminalSessionManager;

    @Scheduled(cron = "0 */1 * * * ?")
    private void checkHeartbeat() {
        terminalSessionManager.getSessionHolder().forEach((k, v) -> {
            if (!v.isDown()) {
                return;
            }
            log.info("terminal 心跳检查down token: {}", k);
            try {
                v.heartbeatDownClose();
            } catch (Exception e) {
                log.error("terminal 心跳检查断连失败 token: {}", k, e);
            }
        });
    }

}
