package com.orion.ops.constant.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp打包传输类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/6 15:20
 */
@Getter
@AllArgsConstructor
public enum SftpPackageType {

    /**
     * 上传文件
     */
    UPLOAD(1),

    /**
     * 下载文件
     */
    DOWNLOAD(2),

    /**
     * 全部
     */
    ALL(3),

    ;

    private final Integer type;

    public static SftpPackageType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (SftpPackageType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
