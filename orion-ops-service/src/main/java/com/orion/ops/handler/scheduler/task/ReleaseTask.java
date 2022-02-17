package com.orion.ops.handler.scheduler.task;

import com.orion.ops.service.api.ApplicationReleaseService;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * 发布任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 10:25
 */
@Slf4j
public class ReleaseTask implements Runnable {

    protected static ApplicationReleaseService applicationReleaseService = SpringHolder.getBean(ApplicationReleaseService.class);

    private Long releaseId;

    public ReleaseTask(Long releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public void run() {
        log.info("定时执行发布任务-触发 releaseId: {}", releaseId);
        applicationReleaseService.runnableAppRelease(releaseId, true);
    }

}
