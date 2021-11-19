package com.orion.ops.consts;

import com.orion.utils.collect.Lists;

import java.util.List;

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

    public static final String PACKAGE_DIR = "/package";

    public static final String ROOT = "root";

    public static final Integer FORBID_DELETE_CAN = 1;

    public static final Integer FORBID_DELETE_NOT = 2;

    public static final String LOGIN_TOKEN = "O-Login-Token";

    public static final String LOGIN_TOKEN_ENC_KEY = "LoginToken";

    public static final int EXEC_COMMAND_OMIT = 80;

    public static final int TEMPLATE_OMIT = 80;

    public static final int TAIL_OFFSET_LINE = 300;

    public static final Long HOST_MACHINE_ID = 1L;

    public static final String SWAP_FILE_SUFFIX = ".swp";

    public static final String SECRET_KEY_SUFFIX = "_id_rsa";

    public static final Integer IS_DEFAULT = 1;

    public static final Integer INCREMENT = 1;

    public static final Integer DECREMENT = 2;

    public static final Integer CONFIGURED = 1;

    public static final Integer NOT_CONFIGURED = 2;

    public static final Integer ADD = 1;

    public static final Integer UPDATE = 2;

    public static final Integer VCS_COMMIT_LIMIT = 15;

    public static final String COPY = "Copy";

    public static final String ROLLBACK = "Rollback";

    public static final String CONNECT = "建立连接";

    public static final String COMPRESS_LIST_FILE = "压缩清单.txt";

    public static final Integer TERMINATED_EXIT_CODE = -1;

    /**
     * 不安全的文件夹
     */
    public static final List<String> UNSAFE_FS_DIR = Lists.of(
            "/", "/bin", "/usr",
            "/sbin", "/etc", "/tmp",
            "/lib", "/var", "/home",
            "/opt", "/root", "/run",
            "/lib64", "/lost+found", "/media",
            "/mnt", "/proc", "/sys",
            "/svr", "/dev", "/boot",
            "/usr/bin", "/usr/include", "/usr/lib",
            "/usr/local", "/usr/sbin", ".tmp");

}
