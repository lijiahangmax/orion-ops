package com.orion.ops.constant.scheduler;

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
