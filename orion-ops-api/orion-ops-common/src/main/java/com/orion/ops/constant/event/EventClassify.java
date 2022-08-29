package com.orion.ops.constant.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件分类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/23 17:40
 */
@AllArgsConstructor
@Getter
public enum EventClassify {

    /**
     * 认证操作
     */
    AUTHENTICATION(5, "认证操作"),

    /**
     * 机器操作
     */
    MACHINE(10, "机器操作"),

    /**
     * 机器环境变量操作
     */
    MACHINE_ENV(15, "机器环境变量操作"),

    /**
     * 秘钥操作
     */
    MACHINE_KEY(20, "秘钥操作"),

    /**
     * 代理操作
     */
    MACHINE_PROXY(25, "代理操作"),

    /**
     * 机器监控
     */
    MACHINE_MONITOR(27, "机器监控"),

    /**
     * 机器报警
     */
    MACHINE_ALARM(28, "机器报警"),

    /**
     * 终端操作
     */
    TERMINAL(30, "终端操作"),

    /**
     * sftp 操作
     */
    SFTP(35, "sftp 操作"),

    /**
     * 批量执行操作
     */
    EXEC(40, "批量执行操作"),

    /**
     * tail 文件操作
     */
    TAIL(45, "tail 文件操作"),

    /**
     * 模板操作
     */
    TEMPLATE(50, "模板操作"),

    /**
     * webhook 操作
     */
    WEBHOOK(52, "webhook操作"),

    /**
     * 用户操作
     */
    USER(55, "用户操作"),

    /**
     * 报警组操作
     */
    ALARM_GROUP(57, "报警组操作"),

    /**
     * 应用操作
     */
    APP(60, "应用操作"),

    /**
     * 环境操作
     */
    PROFILE(65, "环境操作"),

    /**
     * 应用环境变量操作
     */
    APP_ENV(70, "应用环境变量操作"),

    /**
     * 应用仓库操作
     */
    REPOSITORY(75, "应用仓库操作"),

    /**
     * 应用构建操作
     */
    BUILD(80, "应用构建操作"),

    /**
     * 应用发布操作
     */
    RELEASE(85, "应用发布操作"),

    /**
     * 应用流水线
     */
    PIPELINE(88, "应用流水线"),

    /**
     * 系统环境变量操作
     */
    SYSTEM_ENV(90, "系统环境变量操作"),

    /**
     * 系统操作
     */
    SYSTEM(95, "系统操作"),

    /**
     * 调度操作
     */
    SCHEDULER(100, "调度操作"),

    /**
     * 数据清理
     */
    DATA_CLEAR(110, "数据清理"),

    /**
     * 数据导入
     */
    DATA_IMPORT(120, "数据导入"),

    /**
     * 数据导出
     */
    DATA_EXPORT(130, "数据导出"),

    ;

    /**
     * 分类
     */
    private final Integer classify;

    private final String label;

    public static EventClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (EventClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }

}
