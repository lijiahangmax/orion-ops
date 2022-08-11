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
     * 未安装
     */
    NOT_INSTALL(1),

    /**
     * 安装中
     */
    INSTALLING(2),

    /**
     * 未运行
     */
    NOT_START(3),

    /**
     * 运行中
     */
    STARTED(4),

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
