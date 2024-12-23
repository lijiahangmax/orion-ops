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

import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.utils.FileCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件自动清理任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 1:19
 */
@Slf4j
@Component
public class AutoCleanFileTask {

    @Scheduled(cron = "0 30 1 * * ?")
    private void cleanHandler() {
        if (!EnableType.of(SystemEnvAttr.ENABLE_AUTO_CLEAN_FILE.getValue()).getValue()) {
            return;
        }
        log.info("自动清理文件-开始 {}", Dates.current());
        FileCleaner.cleanAll();
        log.info("自动清理文件-结束 {}", Dates.current());
    }

}
