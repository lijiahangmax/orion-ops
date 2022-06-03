package com.orion.ops.consts.clear;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据清理区间
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:29
 */
@Getter
@AllArgsConstructor
public enum DataClearRange {

    /**
     * 保留天数
     */
    DAY(10),

    /**
     * 保留条数
     */
    TOTAL(20),

    /**
     * 关联数据
     */
    REL_ID(30),

    ;

    private final Integer range;

    public static DataClearRange of(Integer range) {
        if (range == null) {
            return null;
        }
        for (DataClearRange value : values()) {
            if (value.range.equals(range)) {
                return value;
            }
        }
        return null;
    }

}
