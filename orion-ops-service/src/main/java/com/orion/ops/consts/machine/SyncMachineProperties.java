package com.orion.ops.consts.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同步机器属性
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 18:00
 */
@Getter
@AllArgsConstructor
public enum SyncMachineProperties {

    /**
     * 机器名称
     */
    MACHINE_NAME("machineName", "cat /etc/hostname"),

    /**
     * 系统版本
     */
    SYSTEM_VERSION("systemVersion", "uname -a");

    private String prop;

    private String command;

    public static SyncMachineProperties of(String prop) {
        if (prop == null) {
            return null;
        }
        for (SyncMachineProperties value : values()) {
            if (value.prop.equals(prop)) {
                return value;
            }
        }
        return null;
    }

}
