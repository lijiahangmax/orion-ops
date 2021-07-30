package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 版本控制工具类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/6 10:48
 */
@AllArgsConstructor
@Getter
public enum VcsType {

    /**
     * git
     */
    GIT("GIT"),

    ;

    private final String type;

    public static VcsType of(String type) {
        if (type == null) {
            return null;
        }
        for (VcsType value : values()) {
            if (value.type.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

}
