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

    public static final String UNAUTHORIZED = "会话过期";

    public static final String NO_PERMISSION = "无操作权限";

    public static final String FILE_ABSENT = "文件不存在";

    public static final String IP_BAN = "当前IP已被封禁";

    public static final String ABSENT_PARAM = "参数缺失";

    public static final String INVALID_PARAM = "非法参数";

    public static final String PARSE_ERROR = "解析失败";

    public static final String SFTP_OPERATOR_ERROR = "操作失败";

    public static final String VCS_OPERATOR_ERROR = "版本控制仓库操作执行失败";

    public static final String TASK_ERROR = "任务执行异常";

    public static final String CONNECT_ERROR = "建立连接失败";

    public static final String TIMEOUT_ERROR = "处理超时";

    public static final String INTERRUPT_ERROR = "操作中断";

    public static final String USERNAME_PASSWORD_ERROR = "用户名或密码错误";

    public static final String USER_DISABLED = "用户已被禁用";

    public static final String USER_LOCKED = "用户已被锁定";

    public static final String UNKNOWN_USER = "未查询到用户信息";

    public static final String USERNAME_PRESENT = "用户名已存在";

    public static final String BEFORE_PASSWORD_EMPTY = "原密码为空";

    public static final String BEFORE_PASSWORD_ERROR = "原密码错误";

    public static final String UNSAFE_OPERATOR = "不安全的操作";

    public static final String UNSUPPORTED_OPERATOR = "不支持的操作";

    public static final String ENCRYPT_ERROR = "数据加密异常";

    public static final String DECRYPT_ERROR = "数据解密异常";

    public static final String UNKNOWN_DATA = "未查询到数据";

    public static final String INVALID_MACHINE = "未知的机器";

    public static final String MACHINE_NOT_ENABLE = "机器未启用";

    public static final String INVALID_PROXY = "未查询到代理信息";

    public static final String INVALID_PTY = "终端类型不合法";

    public static final String EXCEPTION_MESSAGE = "系统异常";

    public static final String AUTH_EXCEPTION_MESSAGE = "认证失败";

    public static final String IO_EXCEPTION_MESSAGE = "网络异常";

    public static final String UNCONNECTED = "未建立连接";

    public static final String EXEC_TASK_ABSENT = "未查询到任务信息";

    public static final String EXEC_TASK_THREAD_ABSENT = "未查询到任务进程";

    public static final String TEMPLATE_ABSENT = "未查询到模板信息";

    public static final String HISTORY_VALUE_ABSENT = "未查询到历史值信息";

    public static final String METADATA_ABSENT = "元数据缺失";

    public static final String ENV_ABSENT = "环境变量不存在";

    public static final String TOKEN_EMPTY = "token为空";

    public static final String TOKEN_EXPIRE = "token过期";

    public static final String SESSION_EXPIRE = "会话过期";

    public static final String PATH_NOT_NORMALIZE = "路径不合法";

    public static final String FILE_NOT_FOUND = "文件 {} 不存在";

    public static final String TRANSFER_ITEM_EMPTY = "未找到可传输对象";

    public static final String UNSELECTED_TRANSFER_LOG = "未找到传输对象";

    public static final String FILE_TOO_LARGE = "文件过大";

    public static final String NAME_PRESENT = "名称重复";

    public static final String FORBID_DELETE = "禁止删除";

    public static final String APP_ABSENT = "应用不存在";

    public static final String PROFILE_ABSENT = "环境不存在";

    public static final String APP_PROFILE_NOT_CONFIGURED = "应用环境还未配置";

    public static final String CHECKOUT_ACTION_PRESENT = "检出操作只能有一个";

    public static final String TRANSFER_ACTION_PRESENT = "传输操作只能有一个";

    public static final String ADD_SECRET_KEY_ERROR = "添加秘钥失败";

    public static final String UPDATE_SECRET_KEY_ERROR = "修改秘钥失败";

    public static final String REMOVE_SECRET_KEY_ERROR = "删除秘钥失败";

    public static final String MOUNT_SECRET_KEY_ERROR = "挂载秘钥失败";

    public static final String DUMP_SECRET_KEY_ERROR = "卸载秘钥失败";

    public static final String TEMP_MOUNT_SECRET_KEY_ERROR = "临时挂载秘钥失败";

    public static final String AUTO_AUDIT_RESOLVE = "自动审核通过";

    public static final String AUDIT_NOT_REQUIRED = "无需审核";

    public static final String RELEASE_ABSENT = "发布单不存在";

    public static final String RELEASE_MACHINE_ABSENT = "发布机器不存在";

    public static final String OPERATOR_NOT_ALL_SUCCESS = "未全部执行成功";

    public static final String UNKNOWN_RELEASE_MACHINE = "未知的发布机器";

    public static final String ILLEGAL_STATUS = "当前状态不支持此操作";

    public static final String FILE_ABSENT_UNABLE_ROLLBACK = "产物丢失无法回滚";

    public static final String VCS_INIT_ERROR = "仓库初始化失败";

    public static final String VCS_PATH_ABSENT = "仓库目录不存在";

    public static final String VCS_UNABLE_CONNECT = "无法连接到远程仓库";

    public static final String VCS_UNINITIALIZED = "仓库未初始化";

    public static final String VCS_INITIALIZED = "远程仓库已初始化";

    public static final String VCS_INITIALIZING = "远程仓库初始化中";

    public static final String CHECKOUT_ERROR = "git clone 检出失败";

    public static final String RESET_ERROR = "git reset 操作失败";

    public static final String BUILD_ABSENT = "构建版本不存在";

    public static final String BUNDLE_FILE_ABSENT = "构建产物不存在";

    public static final String BUNDLE_ZIP_FILE_ABSENT = "构建产物压缩文件不存在";

    public static final String UNABLE_CONFIG_RELEASE_MACHINE = "发布机器未配置";

    public static final String NO_SUCH_FILE = "未找到文件或目录";

    public static final String TASK_PRESENT = "任务已存在";

    public static final String INVALID_CONFIG = "配置不合法";

    public static final String ERROR_EXPRESSION = "表达式错误";

    public static final String TIMED_GREATER_THAN_NOW = "定时操作时间不能小于当前时间";

    public static final String TASK_NOT_ENABLED = "调度任务未启用";

    public static final String SESSION_PRESENT = "会话不存在";

    public static final String UPLOAD_TOO_LARGE = "上传文件大小不能大于 {}MB";

}
