package com.orion.ops.constant.ws;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Valid;
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
     * 握手
     */
    ACK("010"),

    /**
     * 连接成功
     */
    CONNECTED("015"),

    /**
     * pong
     */
    PONG("020"),

    /**
     * 正常返回
     */
    OK("100"),

    /**
     * 未知操作
     */
    UNKNOWN_OPERATE("200"),

    /**
     * 参数缺失
     */
    MISS_ARGUMENT("210"),

    /**
     * 不合法的请求体
     */
    ILLEGAL_BODY("220"),

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
