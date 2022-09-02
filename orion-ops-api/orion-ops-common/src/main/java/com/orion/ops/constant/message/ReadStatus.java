package com.orion.ops.constant.message;

import com.orion.ops.constant.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:36
 */
@AllArgsConstructor
@Getter
public enum ReadStatus {

    /**
     * 未读
     */
    UNREAD(1, CnConst.UNREAD),

    /**
     * 已读
     */
    READ(2, CnConst.READ),

    ;

    private final Integer status;

    private final String label;

    public static ReadStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ReadStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
