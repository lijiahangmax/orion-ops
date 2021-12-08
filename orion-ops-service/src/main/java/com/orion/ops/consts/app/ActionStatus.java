package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 21:28
 */
@AllArgsConstructor
@Getter
public enum ActionStatus {

    /**
     * 未开始
     */
    WAIT(10),

    /**
     * 进行中
     */
    RUNNABLE(20),

    /**
     * 已完成
     */
    FINISH(30),

    /**
     * 执行失败
     */
    FAILURE(40),

    /**
     * 已跳过
     */
    SKIPPED(50),

    /**
     * 已终止
     */
    TERMINATED(60),

    ;

    private final Integer status;

    public static ActionStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ActionStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
