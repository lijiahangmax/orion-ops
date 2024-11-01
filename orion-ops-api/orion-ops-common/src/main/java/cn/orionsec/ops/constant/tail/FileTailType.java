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
package cn.orionsec.ops.constant.tail;

import cn.orionsec.ops.constant.system.SystemEnvAttr;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * tail 文件类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:48
 */
@AllArgsConstructor
@Getter
public enum FileTailType {

    /**
     * 命令执行日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    EXEC_LOG(10, true),

    /**
     * tail 列表
     */
    TAIL_LIST(20, false),

    /**
     * 应用构建日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_BUILD_LOG(30, true),

    /**
     * 应用发布日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_RELEASE_LOG(40, true),

    /**
     * 调度任务机器日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    SCHEDULER_TASK_MACHINE_LOG(50, true),

    /**
     * 应用操作日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_ACTION_LOG(60, true),

    ;

    private final Integer type;

    /**
     * 是否为本地文件
     */
    private final boolean isLocal;

    public static FileTailType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (FileTailType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
