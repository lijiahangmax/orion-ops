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
package cn.orionsec.ops.task;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.task.impl.PipelineTaskImpl;
import cn.orionsec.ops.task.impl.ReleaseTaskImpl;
import cn.orionsec.ops.task.impl.SchedulerTaskImpl;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * 任务类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 10:16
 */
@AllArgsConstructor
public enum TaskType {

    /**
     * 发布任务
     */
    RELEASE(id -> new ReleaseTaskImpl((Long) id)) {
        @Override
        public String getKey(Object params) {
            return Const.RELEASE + "-" + params;
        }
    },

    /**
     * 调度任务
     */
    SCHEDULER_TASK(id -> new SchedulerTaskImpl((Long) id)) {
        @Override
        public String getKey(Object params) {
            return Const.TASK + "-" + params;
        }
    },

    /**
     * 流水线任务
     */
    PIPELINE(id -> new PipelineTaskImpl((Long) id)) {
        @Override
        public String getKey(Object params) {
            return Const.PIPELINE + "-" + params;
        }
    },

    ;

    private final Function<Object, Runnable> factory;

    /**
     * 创建任务
     *
     * @param params params
     * @return task
     */
    public Runnable create(Object params) {
        return factory.apply(params);
    }

    /**
     * 获取 key
     *
     * @param params params
     * @return key
     */
    public abstract String getKey(Object params);

}
