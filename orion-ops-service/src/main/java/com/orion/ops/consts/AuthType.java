package com.orion.ops.consts;

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
public enum AuthType {

    /**
     * 1 密码
     */
    PASSWORD(1),

    /**
     * 2 秘钥
     */
    KEY(2);

    Integer type;

    public static AuthType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (AuthType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
