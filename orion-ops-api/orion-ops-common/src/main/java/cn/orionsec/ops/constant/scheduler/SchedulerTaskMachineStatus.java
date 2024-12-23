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
package cn.orionsec.ops.constant.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调度任务执行机器状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 9:51
 */
@AllArgsConstructor
@Getter
public enum SchedulerTaskMachineStatus {

    /**
     * 待调度
     */
    WAIT(10),

    /**
     * 执行中
     */
    RUNNABLE(20),

    /**
     * 成功
     */
    SUCCESS(30),

    /**
     * 失败
     */
    FAILURE(40),

    /**
     * 已跳过
     */
    SKIPPED(50),

    /**
     * 已停止
     */
    TERMINATED(60),

    ;

    private final Integer status;

    public static SchedulerTaskMachineStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (SchedulerTaskMachineStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
