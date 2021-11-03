package com.orion.ops.consts.ws;

import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import com.orion.utils.io.Streams;
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
    public static final String S = "|";

    public byte[] get() {
        return Strings.bytes(code);
    }

    public byte[] msg(String body) {
        Valid.notNull(body);
        return this.msg(Strings.bytes(body));
    }

    public byte[] msg(byte[] body) {
        Valid.notNull(body);
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        try {
            o.write(Strings.bytes(code + S));
            o.write(body);
            return o.toByteArray();
        } catch (IOException e) {
            throw Exceptions.ioRuntime(e);
        } finally {
            Streams.close(o);
        }
    }

}
