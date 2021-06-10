package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 历史值类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/3 14:00
 */
@AllArgsConstructor
@Getter
public enum HistoryValueType {

    /**
     * 机器环境变量
     */
    MACHINE_ENV(10),

    /**
     * 命令模板
     */
    COMMAND_TEMPLATE(20),

    ;

    Integer type;

    public static HistoryValueType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (HistoryValueType value : values()) {
            if (type.equals(value.type)) {
                return value;
            }
        }
        return null;
    }

}
