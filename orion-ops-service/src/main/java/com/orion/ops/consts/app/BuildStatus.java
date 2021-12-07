package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 构建状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:04
 */
@AllArgsConstructor
@Getter
public enum BuildStatus {

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
     * 已终止
     */
    TERMINATED(50),

    ;

    private final Integer status;

    public static BuildStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (BuildStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
