package com.orion.ops.consts.machine;

import com.orion.ops.consts.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:29
 */
@Getter
@AllArgsConstructor
public enum MachineAuthType {

    /**
     * 1 密码
     */
    PASSWORD(1, CnConst.PASSWORD),

    /**
     * 2 秘钥
     */
    KEY(2, CnConst.SECRET_KEY);

    private final Integer type;

    private final String label;

    public static MachineAuthType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (MachineAuthType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static MachineAuthType of(String label) {
        if (label == null) {
            return null;
        }
        for (MachineAuthType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
