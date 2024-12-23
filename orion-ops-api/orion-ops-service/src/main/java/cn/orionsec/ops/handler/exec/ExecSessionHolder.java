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
package cn.orionsec.ops.handler.exec;

import cn.orionsec.kit.lang.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * exec 实例持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 23:13
 */
@Component
public class ExecSessionHolder {

    /**
     * key: execId
     * value: IExecHandler
     */
    private final Map<Long, IExecHandler> holder = Maps.newCurrentHashMap();

    /**
     * 添加session
     *
     * @param id      id
     * @param session session
     */
    public void addSession(Long id, IExecHandler session) {
        holder.put(id, session);
    }

    /**
     * 获取session
     *
     * @param id id
     * @return session
     */
    public IExecHandler getSession(Long id) {
        return holder.get(id);
    }

    /**
     * 删除session
     *
     * @param id id
     * @return session
     */
    public IExecHandler removeSession(Long id) {
        return holder.remove(id);
    }

}
