package com.orion.ops.consts;

/**
 * redis key 常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 16:44
 */
public class KeyConst {

    private KeyConst() {
    }

    /**
     * 登陆token
     * auth:login:{id}
     */
    public static final String LOGIN_TOKEN_KEY = "auth:info:{}";

    /**
     * 1 day
     */
    public static final int LOGIN_TOKEN_EXPIRE = 60 * 60 * 24;

    /**
     * terminal访问token
     * terminal:access:{token}
     */
    public static final String TERMINAL_ACCESS_TOKEN = "terminal:access:{}";

    /**
     * 5min
     */
    public static final int TERMINAL_ACCESS_TOKEN_EXPIRE = 60 * 5;

    /**
     * terminal绑定session token
     * terminal:bind:{token}
     */
    public static final String TERMINAL_BIND = "terminal:bind:{}";

    /**
     * 6 h
     */
    public static final int TERMINAL_BIND_EXPIRE = 60 * 60 * 6;

    /**
     * file:tail:access{token}
     */
    public static final String FILE_TAIL_ACCESS = "file:tail:access:{}";

    /**
     * 5 min
     */
    public static final int FILE_TAIL_ACCESS_EXPIRE = 60 * 5;

    /**
     * file:tail:bind:{id}
     */
    public static final String FILE_TAIL_BIND = "file:tail:bind:{}";

    /**
     * 5 min
     */
    public static final int FILE_TAIL_BIND_EXPIRE = 60 * 60 * 6;

    /**
     * file:download:{token}
     */
    public static final String FILE_DOWNLOAD = "file:download:{}";

    /**
     * 5 min
     */
    public static final int FILE_DOWNLOAD_EXPIRE = 60 * 5;

}
