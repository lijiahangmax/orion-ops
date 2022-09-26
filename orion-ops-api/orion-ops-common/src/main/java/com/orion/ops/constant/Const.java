package com.orion.ops.constant;

import com.orion.lang.utils.collect.Lists;

import java.util.List;

/**
 * 常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/30 10:07
 */
public class Const extends com.orion.lang.constant.Const {

    private Const() {
    }

    public static final String ORION_OPS = "orion-ops";

    public static final String KEYS_PATH = ".keys";

    public static final String PIC_PATH = "pic";

    public static final String SCREEN_PATH = "screen";

    public static final String SWAP_PATH = "swap";

    public static final String LOG_PATH = "logs";

    public static final String TEMP_PATH = "temp";

    public static final String REPO_PATH = "repo";

    public static final String DIST_PATH = "dist";

    public static final String MACHINE_MONITOR_AGENT_PATH = "/lib/machine-monitor-agent-latest.jar";

    public static final String TAIL_FILE_PATH = "tail";

    public static final String TERMINAL_DIR = "/terminal";

    public static final String BUILD_DIR = "/build";

    public static final String RELEASE_DIR = "/release";

    public static final String RELEASE_MACHINE_PREFIX = "/machine";

    public static final String EXEC_DIR = "/exec";

    public static final String COMMAND_DIR = "/command";

    public static final String AVATAR_DIR = "/avatar";

    public static final String UPLOAD_DIR = "/upload";

    public static final String DOWNLOAD_DIR = "/download";

    public static final String PACKAGE_DIR = "/package";

    public static final String EVENT_DIR = "/event";

    public static final String TASK_DIR = "/task";

    public static final String IMPORT_DIR = "/import";

    public static final String EXPORT_DIR = "/export";

    public static final String LIB_DIR = "/lib";

    public static final String INSTALL_DIR = "/install";

    public static final String BUILD = "build";

    public static final String RELEASE = "release";

    public static final String PIPELINE = "pipeline";

    public static final String TASK = "task";

    public static final String ACTION = "action";

    public static final String ROOT = "root";

    public static final String EVENT = "event";

    public static final String PACKAGE = "package";

    public static final String PLUGINS = "plugins";

    public static final String CAST_SUFFIX = "cast";

    public static final Integer FORBID_DELETE_CAN = 1;

    public static final Integer FORBID_DELETE_NOT = 2;

    public static final int EXEC_COMMAND_OMIT = 80;

    public static final int TEMPLATE_OMIT = 80;

    public static final int TAIL_OFFSET_LINE = 300;

    public static final Long HOST_MACHINE_ID = 1L;

    public static final Long ROOT_TREE_ID = -1L;

    public static final Integer DEFAULT_TREE_SORT = 1;

    public static final String HOST_MACHINE_TAG = "host";

    public static final String SWAP_FILE_SUFFIX = ".swp";

    public static final String SECRET_KEY_SUFFIX = "_id_rsa";

    public static final Integer IS_DEFAULT = 1;

    public static final Integer CONFIGURED = 1;

    public static final Integer IS_SYSTEM = 1;

    public static final Integer NOT_SYSTEM = 2;

    public static final Integer NOT_CONFIGURED = 2;

    public static final Integer COMMIT_LIMIT = 30;

    public static final Integer BUILD_RELEASE_LIMIT = 20;

    public static final String COPY = "Copy";

    public static final String ROLLBACK = "Rollback";

    public static final String COMPRESS_LIST_FILE = "压缩清单.txt";

    public static final int TRACKER_DELAY_MS = 250;

    public static final int MIN_TRACKER_DELAY_MS = 50;

    public static final int DEFAULT_FILE_CLEAN_THRESHOLD = 60;

    public static final int DEFAULT_LOGIN_TOKEN_EXPIRE_HOUR = 24 * 2;

    public static final int SFTP_UPLOAD_THRESHOLD = 512;

    public static final String GITHUB = "github";

    public static final String GITEE = "gitee";

    public static final String GITLAB = "gitlab";

    public static final String OAUTH2 = "oauth2";

    public static final String SOCKS4 = "socks4";

    public static final String SOCKS5 = "socks5";

    public static final String DEFAULT_SHELL = "/bin/bash";

    public static final String LF_2 = "\n\n";

    public static final String LF_3 = "\n\n\n";

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
