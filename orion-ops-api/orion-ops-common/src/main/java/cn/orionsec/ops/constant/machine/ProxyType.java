/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.constant.machine;

import cn.orionsec.ops.constant.Const;
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
