package com.orion.ops.task.impl;

import com.orion.ops.service.api.ApplicationPipelineRecordService;
import com.orion.spring.SpringHolder;
import com.orion.utils.time.Dates;
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

    protected static ApplicationPipelineRecordService applicationPipelineRecordService = SpringHolder.getBean(ApplicationPipelineRecordService.class);

    private Long pipelineRecordId;

    public PipelineTaskImpl(Long pipelineRecordId) {
        this.pipelineRecordId = pipelineRecordId;
    }

    @Override
    public void run() {
        log.info("定时执行流水线任务-触发 releaseId: {}, time: {}", pipelineRecordId, Dates.current());
        applicationPipelineRecordService.execPipeline(pipelineRecordId, true);
    }

}
