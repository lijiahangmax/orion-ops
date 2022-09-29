package com.orion.ops.constant.ws;

/**
 * ws服务端关闭reason
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/16 15:21
 */
public class WsCloseReason {

    private WsCloseReason() {
    }

    public static final String CLOSED_CONNECTION = "closed connection...";

    public static final String IDENTITY_MISMATCH = "identity mismatch...";

    public static final String AUTHENTICATION_FAILURE = "authentication failure...";

    public static final String REMOTE_SERVER_UNREACHABLE = "remote server unreachable...";

    public static final String CONNECTION_TIMEOUT = "connection timeout...";

    public static final String REMOTE_SERVER_AUTHENTICATION_FAILURE = "remote server authentication failure...";

    public static final String MACHINE_DISABLED = "machine disabled...";

    public static final String UNABLE_TO_CONNECT_REMOTE_SERVER = "unable to connect remote server...";

    public static final String FORCED_OFFLINE = "forced offline...";

}
