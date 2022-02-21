package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 9:21
 */
@Getter
@AllArgsConstructor
public enum EnableType {

    /**
     * 启用
     */
    ENABLED(Boolean.TRUE, "enabled"),

    /**
     * 停用
     */
    DISABLED(Boolean.FALSE, "disabled"),

    ;

    private final Boolean value;

    private final String label;

    public static EnableType of(String label) {
        if (label == null) {
            return DISABLED;
        }
        for (EnableType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return DISABLED;
    }

    public static EnableType of(Boolean value) {
        if (value == null) {
            return DISABLED;
        }
        for (EnableType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DISABLED;
    }

}
