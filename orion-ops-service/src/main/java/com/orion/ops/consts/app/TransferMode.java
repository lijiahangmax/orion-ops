package com.orion.ops.consts.app;

import lombok.Getter;

/**
 * 产物传输方式 (sftp/scp)
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/26 22:16
 */
@Getter
public enum TransferMode {

    /**
     * scp
     */
    SCP,

    /**
     * sftp
     */
    SFTP,

    ;

    TransferMode() {
        this.value = name().toLowerCase();
    }

    private final String value;

    public static TransferMode of(String value) {
        if (value == null) {
            return SCP;
        }
        for (TransferMode type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return SCP;
    }

}
