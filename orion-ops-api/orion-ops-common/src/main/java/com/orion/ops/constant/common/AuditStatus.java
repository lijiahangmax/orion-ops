package com.orion.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:42
 */
@AllArgsConstructor
@Getter
public enum AuditStatus {

    /**
     * 通过
     */
    RESOLVE(10),

    /**
     * 驳回
     */
    REJECT(20),

    ;

    private final Integer status;

    public static AuditStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (AuditStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
