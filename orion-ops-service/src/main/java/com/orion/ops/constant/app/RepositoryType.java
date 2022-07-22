package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用版本仓库工具类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/6 10:48
 */
@AllArgsConstructor
@Getter
public enum RepositoryType {

    /**
     * git
     */
    GIT(1),

    ;

    private final Integer type;

    public static RepositoryType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RepositoryType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
