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
package com.orion.ops.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据清理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 18:25
 */
@Getter
@AllArgsConstructor
public enum DataClearType {

    /**
     * 批量执行命令记录
     */
    BATCH_EXEC(10, "批量执行命令记录"),

    /**
     * 终端日志记录
     */
    TERMINAL_LOG(20, "终端日志记录"),

    /**
     * 定时调度任务执行记录
     */
    SCHEDULER_RECORD(30, "定时调度任务执行记录"),

    /**
     * 应用构建记录
     */
    APP_BUILD(40, "应用构建记录"),

    /**
     * 应用发布记录
     */
    APP_RELEASE(50, "应用发布记录"),

    /**
     * 应用流水线执行记录
     */
    APP_PIPELINE_EXEC(60, "应用流水线执行记录"),

    /**
     * 用户操作日志
     */
    USER_EVENT_LOG(70, "用户操作日志记录"),

    /**
     * 机器报警历史记录
     */
    MACHINE_ALARM_HISTORY(80, "机器报警历史记录"),

    ;

    private final Integer type;

    private final String label;

    public static DataClearType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (DataClearType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
