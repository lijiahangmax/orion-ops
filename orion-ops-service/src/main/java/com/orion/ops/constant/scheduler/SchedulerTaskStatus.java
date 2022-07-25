package com.orion.ops.constant.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调度任务执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 9:47
 */
@AllArgsConstructor
@Getter
public enum SchedulerTaskStatus {

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
     * 已停止
     */
    TERMINATED(50),

    ;

    private final Integer status;

    public static SchedulerTaskStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (SchedulerTaskStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
