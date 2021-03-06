package com.orion.ops.consts;

/**
 * 常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/30 10:07
 */
public class Const extends com.orion.constant.Const {

    private Const() {
    }

    public static final String ORION_OPS = "orion_ops";

    public static final String LOG_PATH = "logs";

    public static final String KEYS_PATH = ".keys";

    public static final String DIST_PATH = "dist";

    public static final String PIC_PATH = "pic";

    public static final String TEMP_PATH = "temp";

    public static final String SWAP_PATH = "swap";

    public static final String TERMINAL_LOG_DIR = "/terminal";

    public static final String RELEASE_LOG_DIR = "/release";

    public static final String RELEASE_HOST_LOG_PREFIX = "/host";

    public static final String RELEASE_TARGET_LOG_PREFIX = "/target";

    public static final String RELEASE_ACTION_LOG_PREFIX = "/action";

    public static final String EXEC_LOG_DIR = "/exec";

    public static final String COMMAND_LOG_DIR = "/command";

    public static final String AVATAR_PIC_DIR = "/avatar";

    public static final String UPLOAD_DIR = "/upload";

    public static final String DOWNLOAD_DIR = "/download";

    public static final String ROOT = "root";

    public static final Integer FORBID_DELETE_CAN = 1;

    public static final Integer FORBID_DELETE_NOT = 2;

    public static final String LOGIN_TOKEN = "O-Login-Token";

    public static final String LOGIN_TOKEN_ENC_KEY = "LoginToken";

    public static final int TAIL_OFFSET_LINE = 300;

    public static final Long HOST_MACHINE_ID = 1L;

    public static final String SWAP_FILE_SUFFIX = ".swp";

    public static final String SECRET_KEY_SUFFIX = "_id_rsa";

    public static final Integer IS_DEFAULT = 1;

    public static final Integer INCR = 1;

    public static final Integer DECR = 2;

    public static final Integer CONFIGURED = 1;

    public static final Integer NOT_CONFIGURED = 2;

    public static final Integer ADD = 1;

    public static final Integer UPDATE = 2;

    public static final Integer VCS_COMMIT_LIMIT = 15;

    public static final String COPY = "Copy";

    public static final String ROLLBACK = "Rollback";

    public static final String CONNECT = "建立连接";

}
