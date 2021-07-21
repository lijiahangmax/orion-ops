package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * app步骤执行状态
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
    WAIT_RUNNABLE(10),

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
    EXCEPTION(40),

    /**
     * 已跳过
     */
    SKIPPED(50),

    ;

    Integer status;

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
