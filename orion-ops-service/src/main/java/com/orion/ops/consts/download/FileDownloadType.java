package com.orion.ops.consts.download;

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
public enum FileDownloadType {

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

    /**
     * sftp 下载文件
     */
    SFTP_DOWNLOAD(40),

    ;

    /**
     * 类型
     */
    private final Integer type;

    public static FileDownloadType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (FileDownloadType value : values()) {
            if (type.equals(value.type)) {
                return value;
            }
        }
        return null;
    }

}
