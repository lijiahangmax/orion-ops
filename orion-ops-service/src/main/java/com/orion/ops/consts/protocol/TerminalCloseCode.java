package com.orion.ops.consts.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.socket.CloseStatus;

/**
 * 关闭code
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 21:35
 */
@AllArgsConstructor
@Getter
public enum TerminalCloseCode {

    /**
     * 未查询到token
     */
    INCORRECT_TOKEN(4100, Reasons.CLOSED_CONNECTION),

    /**
     * 伪造token
     */
    FORGE_TOKEN(4120, Reasons.CLOSED_CONNECTION),

    /**
     * 未知的连接
     */
    UNKNOWN_CONNECT(4130, Reasons.CLOSED_CONNECTION),

    /**
     * 认证失败 id不匹配
     */
    IDENTITY_MISMATCH(4140, Reasons.IDENTITY_MISMATCH),

    /**
     * 认证信息不匹配
     */
    VALID(4150, Reasons.AUTHENTICATION_FAILURE),

    /**
     * 连接远程服务器失败
     */
    CONNECTED_FAILURE(4200, Reasons.REMOTE_SERVER_UNREACHABLE),

    /**
     * 远程服务器认证失败
     */
    CONNECTED_AUTH_FAILURE(4205, Reasons.REMOTE_SERVER_AUTHENTICATION_FAILURE),

    /**
     * 远程服务器认证出现异常
     */
    CONNECTED_EXCEPTION(4210, Reasons.UNABLE_TO_CONNECT_REMOTE_SERVER),

    /**
     * 打开shell出现异常
     */
    OPEN_SHELL_EXCEPTION(4220, Reasons.UNABLE_TO_CONNECT_REMOTE_SERVER),

    /**
     * 服务出现异常
     */
    RUNTIME_EXCEPTION(4300, Reasons.CLOSED_CONNECTION),

    /**
     * 服务出现异常 (已认证)
     */
    RUNTIME_VALID_EXCEPTION(4301, Reasons.CLOSED_CONNECTION),

    /**
     * 心跳结束
     */
    HEART_DOWN(4310, Reasons.CLOSED_CONNECTION),

    /**
     * 用户关闭
     */
    DISCONNECT(4320, Reasons.CLOSED_CONNECTION),

    /**
     * 流结束回调
     */
    EOF_CALLBACK(4330, Reasons.CLOSED_CONNECTION),

    /**
     * 读取失败
     */
    READ_EXCEPTION(4335, Reasons.CLOSED_CONNECTION),

    /**
     * 强制下线
     */
    FORCED_OFFLINE(4500, Reasons.FORCED_OFFLINE),

    ;

    private int code;

    private String reason;

    public CloseStatus close() {
        return new CloseStatus(code, reason);
    }

    public static TerminalCloseCode of(int code) {
        for (TerminalCloseCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    private static class Reasons {

        private static final String CLOSED_CONNECTION = "closed connection...";

        private static final String IDENTITY_MISMATCH = "identity mismatch...";

        private static final String AUTHENTICATION_FAILURE = "authentication failure...";

        private static final String REMOTE_SERVER_UNREACHABLE = "remote server unreachable...";

        private static final String REMOTE_SERVER_AUTHENTICATION_FAILURE = "remote server authentication failure...";

        private static final String UNABLE_TO_CONNECT_REMOTE_SERVER = "unable to connect remote server...";

        private static final String FORCED_OFFLINE = "forced offline...";

    }

}
