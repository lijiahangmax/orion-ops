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
     * 2 day
     */
    public static final int LOGIN_TOKEN_EXPIRE = 60 * 60 * 24 * 2;

    /**
     * terminal访问token
     * terminal:access:{token}
     */
    public static final String TERMINAL_ACCESS_TOKEN = "terminal:access:{}";

    /**
     * 5 min
     */
    public static final int TERMINAL_ACCESS_TOKEN_EXPIRE = 60 * 5;

    /**
     * terminal:id:{id}
     */
    public static final String TERMINAL_ID = "terminal:id:{}";

    /**
     * 1 min
     */
    public static final int TERMINAL_ID_EXPIRE = 60;

    /**
     * file:download:{token}
     */
    public static final String FILE_DOWNLOAD = "file:download:{}";

    /**
     * 5 min
     */
    public static final int FILE_DOWNLOAD_EXPIRE = 60 * 5;

    /**
     * file:tail:{token}
     */
    public static final String FILE_TAIL = "file:tail:{}";

    /**
     * 5 min
     */
    public static final int FILE_TAIL_EXPIRE = 60 * 5;

}
