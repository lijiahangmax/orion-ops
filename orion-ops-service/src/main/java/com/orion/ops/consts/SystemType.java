package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统类型枚举
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:27
 */
@Getter
@AllArgsConstructor
public enum SystemType {

    /**
     * linux
     */
    LINUX(1),

    /**
     * windows
     */
    WINDOWS(2);

    Integer type;

    public static SystemType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (SystemType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
