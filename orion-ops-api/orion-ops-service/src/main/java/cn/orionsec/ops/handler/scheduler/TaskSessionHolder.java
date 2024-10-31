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
package cn.orionsec.ops.handler.scheduler;

import cn.orionsec.kit.lang.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务会话持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 21:05
 */
@Component
public class TaskSessionHolder {

    /**
     * key: recordId
     * value: ITaskProcessor
     */
    private final Map<Long, ITaskProcessor> holder = Maps.newCurrentHashMap();

    /**
     * 添加session
     *
     * @param id      id
     * @param session session
     */
    public void addSession(Long id, ITaskProcessor session) {
        holder.put(id, session);
    }

    /**
     * 获取session
     *
     * @param id id
     * @return session
     */
    public ITaskProcessor getSession(Long id) {
        return holder.get(id);
    }

    /**
     * 删除session
     *
     * @param id id
     * @return session
     */
    public ITaskProcessor removeSession(Long id) {
        return holder.remove(id);
    }

}
