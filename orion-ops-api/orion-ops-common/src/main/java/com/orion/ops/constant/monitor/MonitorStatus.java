package com.orion.ops.constant.monitor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 监控状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:01
 */
@Getter
@AllArgsConstructor
public enum MonitorStatus {

    /**
     * 未启动
     */
    NOT_START(1),

    /**
     * 启动中
     */
    STARTING(2),

    /**
     * 运行中
     */
    RUNNING(3),

    ;

    private final Integer status;

    public static MonitorStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (MonitorStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
