package com.orion.ops.consts.app;

import lombok.Getter;

/**
 * 文件传输类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/21 10:17
 */
@Getter
public enum TransferFileType {

    /**
     * 文件 / 文件夹
     */
    NORMAL,

    /**
     * 文件夹 zip 文件
     */
    ZIP,

    ;

    TransferFileType() {
        this.value = name().toLowerCase();
    }

    private final String value;

    public static TransferFileType of(String value) {
        if (value == null) {
            return NORMAL;
        }
        for (TransferFileType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return NORMAL;
    }

}
