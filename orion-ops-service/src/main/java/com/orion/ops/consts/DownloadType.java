package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 下载类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:09
 */
@AllArgsConstructor
@Getter
public enum DownloadType {

    /**
     * 秘钥
     */
    SECRET_KEY(10),

    /**
     * terminal 日志
     */
    TERMINAL_LOG(20),

    /**
     * 命令 执行日志
     */
    EXEC_LOG(30),

    ;

    /**
     * 类型
     */
    Integer type;

    public static DownloadType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (DownloadType value : values()) {
            if (type.equals(value.type)) {
                return value;
            }
        }
        return null;
    }

}
