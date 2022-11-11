package com.orion.ops.task.fixed;

import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.common.EnableType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.utils.FileCleaner;
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
