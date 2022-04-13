package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流水线状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 13:47
 */
@AllArgsConstructor
@Getter
public enum PipelineStatus {

    /**
     * 待审核
     */
    WAIT_AUDIT(10),

    /**
     * 审核驳回
     */
    AUDIT_REJECT(20),

    /**
     * 待执行
     */
    WAIT_RUNNABLE(30),

    /**
     * 待调度
     */
    WAIT_SCHEDULE(35),

    /**
     * 执行中
     */
    RUNNABLE(40),

    /**
     * 执行完成
     */
    FINISH(50),

    /**
     * 执行停止
     */
    TERMINATED(60),

    /**
     * 执行失败
     */
    FAILURE(70),

    ;

    private final Integer status;

    public static PipelineStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (PipelineStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
