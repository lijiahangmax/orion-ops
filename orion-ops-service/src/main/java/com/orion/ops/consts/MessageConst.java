package com.orion.ops.consts;

/**
 * 消息常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:26
 */
public class MessageConst {

    private MessageConst() {
    }

    public static final String UNAUTHORIZED = "未认证";

    public static final String NO_PERMISSION = "无权限";

    public static final String FILE_MISSING = "文件丢失";

    public static final String MISSING_PARAM = "参数缺失";

    public static final String INVALID_PARAM = "非法参数";

    public static final String SFTP_OPERATOR_ERROR = "操作异常";

    public static final String INVALID_MACHINE = "未知的机器";

    public static final String INVALID_PROXY = "未查询到代理信息";

    public static final String UNABLE_SYNC_PROP = "无法同步属性";

    public static final String INVALID_PTY = "终端类型不合法";

    public static final String EXCEPTION_MESSAGE = "系统繁忙";

    public static final String AUTH_EXCEPTION_MESSAGE = "认证失败";

    public static final String IO_EXCEPTION_MESSAGE = "网络异常";

    public static final String CONN_EXCEPTION_MESSAGE = "建立连接失败";

    public static final String EXEC_TASK_MISSING = "未查询到任务信息";

    public static final String TEMPLATE_MISSING = "未查询到模板信息";

    public static final String HISTORY_VALUE_MISSING = "未查询到历史值信息";

    public static final String METADATA_MISSING = "元数据缺失";

    public static final String MACHINE_ENV_EXIST = "环境变量已存在";

    public static final String MACHINE_ENV_MISSING = "环境变量不存在";

    public static final String TOKEN_EMPTY = "token为空";

    public static final String TOKEN_EXPIRE = "token过期";

    public static final String SESSION_EXPIRE = "会话过期";

    public static final String PATH_NOT_NORMALIZE = "路径不合法";

    public static final String DEL_ROOT_PATH = "你TM敢删除根目录?";

    public static final String FILE_NOTFOUND = "文件不存在";

    public static final String UNSELECTED_TRANSFER_LOG = "未找到传输对象";

    public static final String UNSELECTED_TRANSFER_PROCESSOR = "未找到传输进程";

    public static final String FILE_TOO_LARGE = "文件过大";

    public static final String INVALID_STATUS = "状态不合法";

    public static final String NAME_TAG_PRESENT = "名称或tag重复";

}
