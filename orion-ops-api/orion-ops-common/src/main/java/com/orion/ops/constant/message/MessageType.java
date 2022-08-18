package com.orion.ops.constant.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:35
 */
@AllArgsConstructor
@Getter
public enum MessageType {

    // -------------------- 系统消息 --------------------

    /**
     * 命令执行完成
     */
    EXEC_SUCCESS(1010, "命令执行完成", MessageClassify.SYSTEM, "<sb 0>${name}</sb> 命令执行完成"),

    /**
     * 命令执行失败
     */
    EXEC_FAILURE(1020, "命令执行失败", MessageClassify.SYSTEM, "<sb 0>${name}</sb> 命令执行失败"),

    /**
     * 版本仓库初始化成功
     */
    REPOSITORY_INIT_SUCCESS(1030, "版本仓库初始化成功", MessageClassify.SYSTEM, "<sb 0>${name}</sb> 仓库初始化成功"),

    /**
     * 版本仓库初始化失败
     */
    REPOSITORY_INIT_FAILURE(1040, "版本仓库初始化失败", MessageClassify.SYSTEM, "<sb 0>${name}</sb> 仓库初始化失败"),

    /**
     * 构建执行成功
     */
    BUILD_SUCCESS(1050, "构建执行成功", MessageClassify.SYSTEM, "<sb 0>${appName}</sb> <sb>#${seq}</sb> 构建成功"),

    /**
     * 构建执行失败
     */
    BUILD_FAILURE(1060, "构建执行失败", MessageClassify.SYSTEM, "<sb 0>${appName}</sb> <sb>#${seq}</sb> 构建失败"),

    /**
     * 发布审批通过
     */
    RELEASE_AUDIT_RESOLVE(1070, "发布审批通过", MessageClassify.SYSTEM, "发布任务 <sb>${title}</sb> 审核已通过"),

    /**
     * 发布审批驳回
     */
    RELEASE_AUDIT_REJECT(1080, "发布审批驳回", MessageClassify.SYSTEM, "发布任务 <sb>${title}</sb> 审核已被驳回"),

    /**
     * 发布执行成功
     */
    RELEASE_SUCCESS(1090, "发布执行成功", MessageClassify.SYSTEM, "发布任务 <sb>${title}</sb> 发布成功"),

    /**
     * 发布执行失败
     */
    RELEASE_FAILURE(1100, "发布执行失败", MessageClassify.SYSTEM, "发布任务 <sb>${title}</sb> 发布失败"),

    /**
     * 应用流水线审批通过
     */
    PIPELINE_AUDIT_RESOLVE(1110, "应用流水线审批通过", MessageClassify.SYSTEM, "应用流水线 <sb>${name}</sb> <sb>${title}</sb> 审核已通过"),

    /**
     * 应用流水线审批驳回
     */
    PIPELINE_AUDIT_REJECT(1120, "应用流水线审批驳回", MessageClassify.SYSTEM, "应用流水线 <sb>${name}</sb> <sb>${title}</sb> 审核已被驳回"),

    /**
     * 应用流水线执行成功
     */
    PIPELINE_EXEC_SUCCESS(1130, "应用流水线执行成功", MessageClassify.SYSTEM, "应用流水线 <sb>${name}</sb> <sb>${title}</sb> 执行成功"),

    /**
     * 应用流水线执行失败
     */
    PIPELINE_EXEC_FAILURE(1140, "应用流水线执行失败", MessageClassify.SYSTEM, "应用流水线 <sb>${name}</sb> <sb>${title}</sb> 执行失败"),

    /**
     * 机器监控插件安装成功
     */
    MACHINE_AGENT_INSTALL_SUCCESS(1150, "机器监控插件安装成功", MessageClassify.SYSTEM, "<sb>${name}</sb> 监控插件安装成功"),

    /**
     * 机器监控插件安装失败
     */
    MACHINE_AGENT_INSTALL_FAILURE(1160, "机器监控插件安装失败", MessageClassify.SYSTEM, "<sb>${name}</sb> 监控插件安装失败, 请手动安装或检查插件配置"),

    // -------------------- 导入通知 --------------------

    /**
     * 机器信息导入成功
     */
    MACHINE_IMPORT_SUCCESS(2010, "机器信息导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的机器信息导入操作执行完成"),

    /**
     * 机器信息导入失败
     */
    MACHINE_IMPORT_FAILURE(2020, "机器信息导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的机器信息导入操作执行失败"),

    /**
     * 机器代理导入成功
     */
    MACHINE_PROXY_IMPORT_SUCCESS(2030, "机器代理导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的机器代理导入操作执行完成"),

    /**
     * 机器代理导入失败
     */
    MACHINE_PROXY_IMPORT_FAILURE(2040, "机器代理导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的机器代理导入操作执行失败"),

    /**
     * 日志文件导入成功
     */
    MACHINE_TAIL_FILE_IMPORT_SUCCESS(2050, "日志文件导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的日志文件导入操作执行完成"),

    /**
     * 日志文件导入失败
     */
    MACHINE_TAIL_FILE_IMPORT_FAILURE(2060, "日志文件导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的日志文件导入操作执行失败"),

    /**
     * 应用环境导入成功
     */
    PROFILE_IMPORT_SUCCESS(2070, "应用环境导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的应用环境导入操作执行完成"),

    /**
     * 应用环境导入失败
     */
    PROFILE_IMPORT_FAILURE(2080, "应用环境导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的应用环境导入操作执行失败"),

    /**
     * 应用信息导入成功
     */
    APPLICATION_IMPORT_SUCCESS(2090, "应用信息导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的应用信息导入操作执行完成"),

    /**
     * 应用信息导入失败
     */
    APPLICATION_IMPORT_FAILURE(2100, "应用信息导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的应用信息导入操作执行失败"),

    /**
     * 版本仓库导入成功
     */
    REPOSITORY_IMPORT_SUCCESS(2110, "版本仓库导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的版本仓库导入操作执行完成"),

    /**
     * 版本仓库导入失败
     */
    REPOSITORY_IMPORT_FAILURE(2120, "版本仓库导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的版本仓库导入操作执行失败"),

    /**
     * 命令模板导入成功
     */
    COMMAND_TEMPLATE_IMPORT_SUCCESS(2130, "命令模板导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的命令模板导入操作执行完成"),

    /**
     * 命令模板导入失败
     */
    COMMAND_TEMPLATE_IMPORT_FAILURE(2140, "命令模板导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的命令模板导入操作执行失败"),

    ;

    private final Integer type;

    private final String label;

    private final MessageClassify classify;

    private final String template;

    public static MessageType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (MessageType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
