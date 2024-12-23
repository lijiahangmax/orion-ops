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
package cn.orionsec.ops.constant.monitor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 监控状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:01
 */
@Getter
@AllArgsConstructor
public enum MonitorStatus {

    /**
     * 未启动
     */
    NOT_START(1),

    /**
     * 启动中
     */
    STARTING(2),

    /**
     * 运行中
     */
    RUNNING(3),

    ;

    private final Integer status;

    public static MonitorStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (MonitorStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
