package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/23 15:40
 */
@AllArgsConstructor
@Getter
public enum ProxyType {

    /**
     * HTTP 代理
     */
    HTTP(1),

    /**
     * SOCKET4 代理
     */
    SOCKET4(2),

    /**
     * SOCKET5 代理
     */
    SOCKET5(3),

    ;

    Integer type;

    public static ProxyType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ProxyType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
