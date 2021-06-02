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

    public static final String INVALID_PARAM = "参数不合法";

    public static final String INVALID_MACHINE = "未查询到机器信息";

    public static final String ROOT = "root";

    public static final Integer FORBID_DELETE_CAN = 1;

    public static final Integer FORBID_DELETE_NOT = 2;

    public static final String LOGIN_TOKEN = "O-Login-Token";

    public static final String LOGIN_TOKEN_ENC_KEY = "LoginToken";

    public static final int LOGIN_TOKEN_EXPIRE = 60 * 60 * 24;

    public static final String EXCEPTION_MESSAGE = "系统繁忙";

    public static final String AUTH_EXCEPTION_MESSAGE = "认证失败";

    public static final String IO_EXCEPTION_MESSAGE = "网络异常";

    public static final String CONN_EXCEPTION_MESSAGE = "连接失败";

}
