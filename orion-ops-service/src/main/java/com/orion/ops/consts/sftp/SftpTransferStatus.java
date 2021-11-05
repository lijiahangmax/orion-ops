package com.orion.ops.consts.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp 传输状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 12:11
 */
@Getter
@AllArgsConstructor
public enum SftpTransferStatus {

    /**
     * 10 等待中
     */
    WAIT(10),

    /**
     * 20 进行中
     */
    RUNNABLE(20),

    /**
     * 30 已暂停
     */
    PAUSE(30),

    /**
     * 40 已完成
     */
    FINISH(40),

    /**
     * 50 传输异常
     */
    ERROR(50),

    ;

    private final Integer status;

    public static SftpTransferStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (SftpTransferStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
