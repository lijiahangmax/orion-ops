package com.orion.ops.consts.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秘钥挂载状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/5/31 16:18
 */
@AllArgsConstructor
@Getter
public enum MountKeyStatus {

    /**
     * 未找到
     */
    NOT_FOUND(1),

    /**
     * 已挂载
     */
    MOUNTED(2),

    /**
     * 未挂载
     */
    DUMPED(3);

    private final Integer status;

    public static MountKeyStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (MountKeyStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
