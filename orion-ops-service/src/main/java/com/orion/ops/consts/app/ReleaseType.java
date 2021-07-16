package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * app发布类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 21:18
 */
@AllArgsConstructor
@Getter
public enum ReleaseType {

    /**
     * 正常发布
     */
    NORMAL(10),

    /**
     * 回滚发布
     */
    ROLLBACK(20),

    ;

    Integer type;

    public static ReleaseType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ReleaseType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
