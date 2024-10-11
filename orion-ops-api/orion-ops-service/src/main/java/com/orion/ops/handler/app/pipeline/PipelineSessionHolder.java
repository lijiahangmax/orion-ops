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
package com.orion.ops.handler.app.pipeline;

import com.orion.lang.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 流水线会话
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 20:00
 */
@Component
public class PipelineSessionHolder {

    /**
     * session
     */
    private final ConcurrentHashMap<Long, IPipelineProcessor> session = Maps.newCurrentHashMap();

    /**
     * 添加 session
     *
     * @param processor processor
     */
    public void addSession(IPipelineProcessor processor) {
        session.put(processor.getTaskId(), processor);
    }

    /**
     * 获取 session
     *
     * @param id id
     * @return session
     */
    public IPipelineProcessor getSession(Long id) {
        return session.get(id);
    }

    /**
     * 移除 session
     *
     * @param id id
     */
    public void removeSession(Long id) {
        session.remove(id);
    }

}
