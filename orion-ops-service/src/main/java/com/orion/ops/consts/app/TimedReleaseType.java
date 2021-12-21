package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时发布类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:09
 */
@AllArgsConstructor
@Getter
public enum TimedReleaseType {

    /**
     * 普通发布
     */
    NORMAL(10),

    /**
     * 定时发布
     */
    TIMED(20),

    ;

    private final Integer type;

    public static TimedReleaseType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TimedReleaseType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
