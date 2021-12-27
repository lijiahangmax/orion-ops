package com.orion.ops.consts;

/**
 * 环境变量常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/30 10:30
 */
public class EnvConst {

    private EnvConst() {
    }

    // -------------------- symbol --------------------

    public static final String SYMBOL = "@";

    // -------------------- prefix --------------------

    public static final String MACHINE_PREFIX = "machine.";

    public static final String APP_PREFIX = "app.";

    public static final String BUILD_PREFIX = "build.";

    public static final String RELEASE_PREFIX = "release.";

    // -------------------- machine --------------------

    public static final String MACHINE_NAME = "machine_name";

    public static final String MACHINE_TAG = "machine_tag";

    public static final String MACHINE_HOST = "machine_host";

    public static final String MACHINE_PORT = "machine_port";

    public static final String MACHINE_USERNAME = "machine_username";

    // -------------------- app --------------------

    public static final String APP_ID = "app_id";

    public static final String APP_NAME = "app_name";

    public static final String APP_TAG = "app_tag";

    public static final String PROFILE_ID = "profile_id";

    public static final String PROFILE_NAME = "profile_name";

    public static final String PROFILE_TAG = "profile_tag";

    // -------------------- build --------------------

    public static final String BUILD_ID = "build_id";

    public static final String BUILD_SEQ = "build_seq";

    public static final String VCS_HOME = "vcs_home";

    // -------------------- release --------------------

    public static final String RELEASE_ID = "release_id";

    public static final String RELEASE_TITLE = "release_title";

    public static final String DIST_PATH = "dist_path";

    public static final String TARGET_DIST_PATH = "target_dist_path";

    // -------------------- tail --------------------

    public static final String OFFSET = "offset";

    public static final String FILE = "file";

    /**
     * 获取替换变量
     *
     * @param name name
     * @return command
     */
    public static String getReplaceVariable(String name) {
        return SYMBOL + "{" + name + "}";
    }

}
