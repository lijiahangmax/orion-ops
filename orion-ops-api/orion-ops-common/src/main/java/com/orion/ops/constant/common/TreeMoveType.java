package com.orion.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 树移动类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/28 17:42
 */
@Getter
@AllArgsConstructor
public enum TreeMoveType {

    /**
     * 移动到内层 上面
     */
    IN_TOP(1),

    /**
     * 移动到内层 下面
     */
    IN_BOTTOM(2),

    /**
     * 移动到节点 上面
     */
    PREV(3),

    /**
     * 移动到节点 下面
     */
    NEXT(4),

    ;

    private final Integer type;

    public static TreeMoveType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TreeMoveType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
