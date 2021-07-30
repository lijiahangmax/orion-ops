package com.orion.ops.consts.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命令执行类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:14
 */
@AllArgsConstructor
@Getter
public enum ExecType {

    /**
     * 批量执行
     */
    BATCH_EXEC(10),

    ;

    /**
     * 类型
     */
    private final Integer type;

    public static ExecType of(Integer type) {
        for (ExecType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
