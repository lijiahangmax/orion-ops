package com.orion.ops.consts.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机器属性
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 18:00
 */
@Getter
@AllArgsConstructor
public enum MachineProperty {

    /**
     * 机器名称
     */
    MACHINE_NAME("machineName", "cat /etc/redhat-release"),

    /**
     * 系统版本
     */
    SYSTEM_VERSION("systemVersion", "uname -a");

    private final String prop;

    private final String command;

    public static MachineProperty of(String prop) {
        if (prop == null) {
            return null;
        }
        for (MachineProperty value : values()) {
            if (value.prop.equals(prop)) {
                return value;
            }
        }
        return null;
    }

}
