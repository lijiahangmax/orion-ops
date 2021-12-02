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
     * <p>
     * auth:login:{id}
     */
    public static final String LOGIN_TOKEN_KEY = "auth:info:{}";

    /**
     * 3 day
     */
    public static final int LOGIN_TOKEN_EXPIRE = 60 * 60 * 24 * 3;

    /**
     * terminal访问token
     * <p>
     * terminal:access:{token}
     */
    public static final String TERMINAL_ACCESS_TOKEN = "terminal:access:{}";

    /**
     * 5min
     */
    public static final int TERMINAL_ACCESS_TOKEN_EXPIRE = 60 * 5;

    /**
     * terminal绑定token
     * <p>
     * terminal:bind:{token}
     */
    public static final String TERMINAL_BIND_TOKEN = "terminal:bind:{}";

    /**
     * 6 h
     */
    public static final int TERMINAL_BIND_EXPIRE = 60 * 60 * 6;

    /**
     * 文件tail访问token
     * <p>
     * file:tail:access{token}
     */
    public static final String FILE_TAIL_ACCESS_TOKEN = "file:tail:access:{}";

    /**
     * 5 min
     */
    public static final int FILE_TAIL_ACCESS_EXPIRE = 60 * 5;

    /**
     * 文件tail绑定token
     * <p>
     * file:tail:bind:{id}
     */
    public static final String FILE_TAIL_BIND_TOKEN = "file:tail:bind:{}";

    /**
     * 6 h
     */
    public static final int FILE_TAIL_BIND_EXPIRE = 60 * 60 * 6;

    /**
     * 文件下载token
     * <p>
     * file:download:{token}
     */
    public static final String FILE_DOWNLOAD_TOKEN = "file:download:{}";

    /**
     * 5 min
     */
    public static final int FILE_DOWNLOAD_EXPIRE = 60 * 5;

    /**
     * sftp会话token
     * <p>
     * "sftp:session:{token}
     */
    public static final String SFTP_SESSION_TOKEN = "sftp:session:{}";

    /**
     * 12 h
     */
    public static final int SFTP_SESSION_EXPIRE = 60 * 60 * 12;

    /**
     * sftp上传请求token
     * <p>
     * "sftp:upload:{token}
     */
    public static final String SFTP_UPLOAD_ACCESS_TOKEN = "sftp:upload:{}";

    /**
     * 5 h
     */
    public static final int SFTP_UPLOAD_ACCESS_TOKEN_EXPIRE = 60 * 60 * 5;

}
