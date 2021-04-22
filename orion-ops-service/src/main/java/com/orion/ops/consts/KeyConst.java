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
     * auth:login:{id}
     */
    public static final String LOGIN_TOKEN_KEY = "auth:info:{}";

    /**
     * terminal:access:{token}
     */
    public static final String TERMINAL_ACCESS_TOKEN = "terminal:access:{}";

    /**
     * terminal:id:{id}
     */
    public static final String TERMINAL_ID = "terminal:id:{}";

}
