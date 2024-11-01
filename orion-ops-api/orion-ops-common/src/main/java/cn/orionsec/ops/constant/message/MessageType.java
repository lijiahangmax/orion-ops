/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.constant.message;

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
    EXEC_SUCCESS(1010, "命令执行完成", MessageClassify.SYSTEM, "机器 <sb>${name}</sb> 命令执行完成"),

    /**
     * 命令执行失败
     */
    EXEC_FAILURE(1020, "命令执行失败", MessageClassify.SYSTEM, "机器 <sb>${name}</sb> 命令执行失败"),

    /**
     * 版本仓库初始化成功
     */
    REPOSITORY_INIT_SUCCESS(1030, "版本仓库初始化成功", MessageClassify.SYSTEM, "仓库 <sb>${name}</sb> 初始化成功"),

    /**
     * 版本仓库初始化失败
     */
    REPOSITORY_INIT_FAILURE(1040, "版本仓库初始化失败", MessageClassify.SYSTEM, "仓库 <sb>${name}</sb> 初始化失败"),

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
    MACHINE_AGENT_INSTALL_SUCCESS(1150, "机器监控插件安装成功", MessageClassify.SYSTEM, "机器 <sb>${name}</sb> 监控插件安装成功"),

    /**
     * 机器监控插件安装失败
     */
    MACHINE_AGENT_INSTALL_FAILURE(1160, "机器监控插件安装失败", MessageClassify.SYSTEM, "机器 <sb>${name}</sb> 监控插件安装失败, 请手动安装或检查插件配置"),

    // -------------------- 数据导入 --------------------

    /**
     * 数据导入成功
     */
    DATA_IMPORT_SUCCESS(2010, "数据导入成功", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的<sb 0>${label}</sb>导入操作执行完成"),

    /**
     * 数据导入失败
     */
    DATA_IMPORT_FAILURE(2020, "数据导入失败", MessageClassify.IMPORT, "您在 <sb>${time}</sb> 进行的<sr 0>${label}</sr>导入操作执行失败"),

    // -------------------- 系统报警 --------------------

    /**
     * 机器发生报警
     */
    MACHINE_ALARM(3010, "机器发生报警", MessageClassify.ALARM, "机器 <sb 0><b>${name}</b></sb>(<sb 0>${host}</sb>) ${time} <sb>${type}</sb>达到<sr><b>${value}%</b></sr>, 请及时排查!"),

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
