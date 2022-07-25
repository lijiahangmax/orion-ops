package com.orion.ops.constant.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:20
 */
@AllArgsConstructor
@Getter
public enum ExecStatus {

    /**
     * 10未开始
     */
    WAITING(10),

    /**
     * 20执行中
     */
    RUNNABLE(20),

    /**
     * 30执行完成
     */
    COMPLETE(30),

    /**
     * 40执行异常
     */
    EXCEPTION(40),

    /**
     * 50执行终止
     */
    TERMINATED(50),

    ;

    private final Integer status;

    public static ExecStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ExecStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
