package com.orion.ops.task.impl;

import com.orion.lang.utils.time.Dates;
import com.orion.ops.service.api.ApplicationPipelineTaskService;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * 流水线任务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/13 16:55
 */
@Slf4j
public class PipelineTaskImpl implements Runnable {

    protected static ApplicationPipelineTaskService applicationPipelineTaskService = SpringHolder.getBean(ApplicationPipelineTaskService.class);

    private final Long pipelineRecordId;

    public PipelineTaskImpl(Long pipelineRecordId) {
        this.pipelineRecordId = pipelineRecordId;
    }

    @Override
    public void run() {
        log.info("定时执行流水线任务-触发 releaseId: {}, time: {}", pipelineRecordId, Dates.current());
        applicationPipelineTaskService.execPipeline(pipelineRecordId, true);
    }

}
