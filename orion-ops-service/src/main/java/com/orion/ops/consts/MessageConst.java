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

    public static final String FILE_ABSENT = "文件丢失";

    public static final String ABSENT_PARAM = "参数缺失";

    public static final String INVALID_PARAM = "非法参数";

    public static final String SFTP_OPERATOR_ERROR = "操作异常";

    public static final String VCS_OPERATOR_ERROR = "无法版本控制信息";

    public static final String TASK_ERROR = "任务执行异常";

    public static final String CONNECT_ERROR = "建立连接失败";

    public static final String TIMEOUT_ERROR = "处理超时";

    public static final String INTERRUPT_ERROR = "操作中断";

    public static final String UNSAFE_OPERATOR = "不安全的操作";

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

    public static final String CONN_EXCEPTION_MESSAGE = "建立连接失败";

    public static final String EXEC_TASK_RUNNABLE_PRESENT = "有正在执行的任务";

    public static final String EXEC_TASK_ABSENT = "未查询到任务信息";

    public static final String TEMPLATE_ABSENT = "未查询到模板信息";

    public static final String HISTORY_VALUE_ABSENT = "未查询到历史值信息";

    public static final String METADATA_ABSENT = "元数据缺失";

    public static final String ENV_PRESENT = "环境变量已存在";

    public static final String ENV_ABSENT = "环境变量不存在";

    public static final String TOKEN_EMPTY = "token为空";

    public static final String TOKEN_EXPIRE = "token过期";

    public static final String SESSION_EXPIRE = "会话过期";

    public static final String PATH_NOT_NORMALIZE = "路径不合法";

    public static final String FILE_NOT_FOUND = "文件 {} 不存在";

    public static final String UNSELECTED_TRANSFER_LOG = "未找到传输对象";

    public static final String UNSELECTED_TRANSFER_PROCESSOR = "未找到传输进程";

    public static final String FILE_TOO_LARGE = "文件过大";

    public static final String INVALID_STATUS = "状态不合法";

    public static final String NAME_PRESENT = "名称重复";

    public static final String FORBID_DELETE = "禁止删除";

    public static final String APP_ABSENT = "应用不存在";

    public static final String PROFILE_ABSENT = "环境不存在";

    public static final String APP_PROFILE_NOT_CONFIGURED = "应用环境还未配置";

    public static final String CODE_PATH_ABSENT = "代码目录不存在";

    public static final String VCS_ROOT_PATH_ABSENT = "版本控制根目录不存在";

    public static final String VCS_ROOT_PATH_UNCONNECTED = "无法连接版本控制根目录";

    public static final String CHECKOUT_ACTION_PRESENT = "检出操作只能有一个";

    public static final String TRANSFER_ACTION_PRESENT = "传输操作只能有一个";

    public static final String CHECKOUT_ACTION_ABSENT = "必须得有检出操作";

    public static final String TRANSFER_ACTION_WRONG_STEP = "传输操作必须要在检出操作之后";

    public static final String HOST_ACTION_WRONG_STEP = "部署步骤需要遵循先宿主机再目标机器的原则";

    public static final String ADD_SECRET_KEY_ERROR = "添加秘钥失败";

    public static final String UPDATE_SECRET_KEY_ERROR = "修改秘钥失败";

    public static final String REMOVE_SECRET_KEY_ERROR = "删除秘钥失败";

    public static final String MOUNT_SECRET_KEY_ERROR = "挂载秘钥失败";

    public static final String DUMP_SECRET_KEY_ERROR = "卸载秘钥失败";

    public static final String TEMP_MOUNT_SECRET_KEY_ERROR = "临时挂载秘钥失败";

    public static final String AUTO_AUDIT_RESOLVE = "自动审核通过";

    public static final String AUDIT_NO_REQUIRED = "无需审核";

    public static final String RELEASE_BILL_ABSENT = "上线单不存在";

    public static final String RELEASE_MACHINE_ABSENT = "上线单机器不存在";

    public static final String UNKNOWN_RELEASE_TYPE = "未知的发布类型";

    public static final String RELEASE_TYPE_UNABLE_COPY = "当前类型不支持此操作";

    public static final String STATUS_UNABLE_ROLLBACK_RELEASE = "当前状态无法回滚";

    public static final String FILE_ABSENT_UNABLE_ROLLBACK_RELEASE = "产物快照丢失无法回滚";

    public static final String STATUS_UNABLE_RUNNABLE_RELEASE = "当前状态无法执行上线单";

}
