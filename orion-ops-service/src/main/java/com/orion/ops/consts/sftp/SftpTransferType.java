package com.orion.ops.consts.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp 操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 12:08
 */
@Getter
@AllArgsConstructor
public enum SftpTransferType {

    /**
     * 10 上传
     */
    UPLOAD(10),

    /**
     * 20 下载
     */
    DOWNLOAD(20),

    /**
     * 30 传输
     */
    TRANSFER(30),

    ;

    Integer type;

    public static SftpTransferType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (SftpTransferType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
