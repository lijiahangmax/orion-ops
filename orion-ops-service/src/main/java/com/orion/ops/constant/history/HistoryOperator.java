package com.orion.ops.constant.history;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 历史操作类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/2 20:26
 */
@Getter
@AllArgsConstructor
public enum HistoryOperator {

    /**
     * 添加
     */
    ADD(1),

    /**
     * 更新
     */
    UPDATE(2),

    /**
     * 删除
     */
    DELETE(3),

    ;

    private final Integer type;

    public static HistoryOperator of(Integer type) {
        if (type == null) {
            return null;
        }
        for (HistoryOperator value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
