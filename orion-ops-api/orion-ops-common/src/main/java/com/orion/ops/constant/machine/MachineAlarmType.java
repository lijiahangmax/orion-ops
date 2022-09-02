package com.orion.ops.constant.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机器报警类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 18:18
 */
@Getter
@AllArgsConstructor
public enum MachineAlarmType {

    /**
     * cpu 使用率
     */
    CPU_USAGE(10, "CPU使用率"),

    /**
     * 内存使用率
     */
    MEMORY_USAGE(20, "内存使用率"),

    ;

    private final Integer type;

    private final String label;

    public static MachineAlarmType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (MachineAlarmType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
