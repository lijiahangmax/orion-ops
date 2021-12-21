package com.orion.ops.consts.app;

import lombok.Getter;

/**
 * 产物为文件夹传输类型 (dir/zip)
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/21 10:17
 */
@Getter
public enum TransferDirType {

    /**
     * 文件夹
     */
    DIR,

    /**
     * 压缩文件
     */
    ZIP,

    ;

    TransferDirType() {
        this.value = name().toLowerCase();
    }

    private final String value;

    public static TransferDirType of(String value) {
        if (value == null) {
            return DIR;
        }
        for (TransferDirType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DIR;
    }

}
