package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * app发布状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 21:23
 */
@AllArgsConstructor
@Getter
public enum ReleaseStatus {

    /**
     * 待审核
     */
    WAIT_AUDIT(10),

    /**
     * 审核驳回
     */
    AUDIT_REJECT(20),

    /**
     * 待发布
     */
    WAIT_RUNNABLE(30),

    /**
     * 发布中
     */
    RUNNABLE(40),

    /**
     * 发布完成
     */
    FINISH(50),

    /**
     * 发布异常
     */
    EXCEPTION(60),

    ;

    Integer status;

    public static ReleaseStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ReleaseStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
