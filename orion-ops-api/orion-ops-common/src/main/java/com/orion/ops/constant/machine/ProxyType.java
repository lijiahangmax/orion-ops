package com.orion.ops.constant.machine;

import com.orion.ops.constant.Const;
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
    HTTP(1, Const.PROTOCOL_HTTP),

    /**
     * SOCKS4 代理
     */
    SOCKS4(2, Const.SOCKS4),

    /**
     * SOCKS5 代理
     */
    SOCKS5(3, Const.SOCKS5),

    ;

    private final Integer type;

    private final String label;

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

    public static ProxyType of(String label) {
        if (label == null) {
            return null;
        }
        for (ProxyType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
