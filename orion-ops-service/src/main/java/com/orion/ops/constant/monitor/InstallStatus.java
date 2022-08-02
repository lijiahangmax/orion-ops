package com.orion.ops.constant.monitor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 安装状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:01
 */
@Getter
@AllArgsConstructor
public enum InstallStatus {

    /**
     * 未安装
     */
    NOT_INSTALL(1),

    /**
     * 安装中
     */
    INSTALLING(2),

    /**
     * 已安装
     */
    INSTALLED(3),

    ;

    private final Integer status;

    public static InstallStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (InstallStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
