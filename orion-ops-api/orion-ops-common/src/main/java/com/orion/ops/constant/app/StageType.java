package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作阶段类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/1 15:48
 */
@AllArgsConstructor
@Getter
public enum StageType {

    /**
     * 构建
     */
    BUILD(10, "构建"),

    /**
     * 发布
     */
    RELEASE(20, "发布"),

    ;

    private final Integer type;

    private final String label;

    public static StageType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (StageType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
