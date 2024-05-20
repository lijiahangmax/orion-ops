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
    AUTHENTICATION(100, "认证操作"),

    /**
     * 用户操作
     */
    USER(110, "用户操作"),

    /**
     * 报警组操作
     */
    ALARM_GROUP(120, "报警组操作"),

    /**
     * 机器操作
     */
    MACHINE(200, "机器操作"),

    /**
     * 机器环境变量操作
     */
    MACHINE_ENV(210, "机器环境变量操作"),

    /**
     * 密钥操作
     */
    MACHINE_KEY(220, "密钥操作"),

    /**
     * 代理操作
     */
    MACHINE_PROXY(230, "代理操作"),

    /**
     * 机器监控
     */
    MACHINE_MONITOR(240, "机器监控"),

    /**
     * 机器报警
     */
    MACHINE_ALARM(250, "机器报警"),

    /**
     * 终端操作
     */
    TERMINAL(260, "终端操作"),

    /**
     * sftp 操作
     */
    SFTP(270, "sftp 操作"),

    /**
     * 批量执行操作
     */
    EXEC(300, "批量执行操作"),

    /**
     * 日志追踪操作
     */
    TAIL(310, "日志追踪操作"),

    /**
     * 调度操作
     */
    SCHEDULER(320, "调度操作"),

    /**
     * 应用操作
     */
    APP(400, "应用操作"),

    /**
     * 环境操作
     */
    PROFILE(410, "环境操作"),

    /**
     * 应用环境变量操作
     */
    APP_ENV(420, "应用环境变量操作"),

    /**
     * 应用仓库操作
     */
    REPOSITORY(430, "应用仓库操作"),

    /**
     * 应用构建操作
     */
    BUILD(440, "应用构建操作"),

    /**
     * 应用发布操作
     */
    RELEASE(450, "应用发布操作"),

    /**
     * 应用流水线
     */
    PIPELINE(460, "应用流水线"),

    /**
     * 模板操作
     */
    TEMPLATE(500, "模板操作"),

    /**
     * webhook 操作
     */
    WEBHOOK(510, "webhook操作"),

    /**
     * 系统操作
     */
    SYSTEM(600, "系统操作"),

    /**
     * 系统环境变量操作
     */
    SYSTEM_ENV(610, "系统环境变量操作"),

    /**
     * 数据清理
     */
    DATA_CLEAR(620, "数据清理"),

    /**
     * 数据导入
     */
    DATA_IMPORT(630, "数据导入"),

    /**
     * 数据导出
     */
    DATA_EXPORT(640, "数据导出"),

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
