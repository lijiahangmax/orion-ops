package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:09
 */
@AllArgsConstructor
@Getter
public enum TimedType {

    /**
     * 普通
     */
    NORMAL(10),

    /**
     * 定时
     */
    TIMED(20),

    ;

    private final Integer type;

    public static TimedType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TimedType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
