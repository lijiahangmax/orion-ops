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
package cn.orionsec.ops.task.impl;

import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.service.api.ApplicationReleaseService;
import lombok.extern.slf4j.Slf4j;

/**
 * 发布任务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 10:25
 */
@Slf4j
public class ReleaseTaskImpl implements Runnable {

    protected static ApplicationReleaseService applicationReleaseService = SpringHolder.getBean(ApplicationReleaseService.class);

    private final Long releaseId;

    public ReleaseTaskImpl(Long releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public void run() {
        log.info("定时执行发布任务-触发 releaseId: {}, time: {}", releaseId, Dates.current());
        applicationReleaseService.runnableAppRelease(releaseId, true, true);
    }

}
