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
package cn.orionsec.ops.task.impl;

import cn.orionsec.ops.service.api.ApplicationPipelineTaskService;
import com.orion.lang.utils.time.Dates;
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
