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
package cn.orionsec.ops.constant.ws;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.Valid;
import lombok.AllArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ws服务端响应常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/16 21:48
 */
@AllArgsConstructor
public enum WsProtocol {

    /**
     * 正常返回
     */
    OK("0"),

    /**
     * 连接成功
     */
    CONNECTED("1"),

    /**
     * ping
     */
    PING("2"),

    /**
     * pong
     */
    PONG("3"),

    /**
     * 未知操作
     */
    ERROR("4"),

    ;

    private final String code;

    /**
     * 分隔符
     */
    public static final String SYMBOL = "|";

    public byte[] get() {
        return Strings.bytes(code);
    }

    public byte[] msg(String body) {
        Valid.notNull(body);
        return this.msg(Strings.bytes(body));
    }

    public byte[] msg(byte[] body) {
        return this.msg(body, 0, body.length);
    }

    public byte[] msg(byte[] body, int offset, int len) {
        Valid.notNull(body);
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            o.write(Strings.bytes(code + SYMBOL));
            o.write(body, offset, len);
            return o.toByteArray();
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        }
    }

}
