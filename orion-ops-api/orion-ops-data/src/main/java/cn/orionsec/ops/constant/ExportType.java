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
package cn.orionsec.ops.constant;

import cn.orionsec.ops.entity.exporter.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * 导出类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 17:17
 */
@Getter
@AllArgsConstructor
public enum ExportType {

    /**
     * 机器信息
     */
    MACHINE_INFO(100,
            "机器信息",
            MachineInfoExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.MACHINE_EXPORT_NAME)),

    /**
     * 机器代理
     */
    MACHINE_PROXY(110,
            "机器代理",
            MachineProxyExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.MACHINE_PROXY_EXPORT_NAME)),

    /**
     * 终端日志
     */
    TERMINAL_LOG(120,
            "终端日志",
            MachineTerminalLogExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.TERMINAL_LOG_EXPORT_NAME)),

    /**
     * 机器报警记录
     */
    MACHINE_ALARM_HISTORY(130,
            "机器报警记录",
            MachineAlarmHistoryExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.MACHINE_ALARM_HISTORY_EXPORT_NAME)),

    /**
     * 应用环境
     */
    APP_PROFILE(200,
            "应用环境",
            ApplicationProfileExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.APP_PROFILE_EXPORT_NAME)),

    /**
     * 应用信息
     */
    APPLICATION(210,
            "应用信息",
            ApplicationExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.APPLICATION_EXPORT_NAME)),

    /**
     * 应用仓库
     */
    APP_REPOSITORY(220,
            "应用仓库",
            ApplicationRepositoryExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.APP_REPOSITORY_EXPORT_NAME)),

    /**
     * 命令模板
     */
    COMMAND_TEMPLATE(300,
            "命令模板",
            CommandTemplateExportDTO.class
            , () -> ExportConst.getFileName(ExportConst.COMMAND_TEMPLATE_EXPORT_NAME)),

    /**
     * 用户操作日志
     */
    USER_EVENT_LOG(310,
            "用户操作日志",
            EventLogExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.USER_EVENT_LOG_EXPORT_NAME)),

    /**
     * 日志文件
     */
    TAIL_FILE(320,
            "日志文件",
            MachineTailFileExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.TAIL_FILE_EXPORT_NAME)),

    /**
     * webhook
     */
    WEBHOOK(330,
            "webhook",
            WebhookExportDTO.class,
            () -> ExportConst.getFileName(ExportConst.WEBHOOK_EXPORT_NAME)),

    ;

    private final Integer type;

    private final String label;

    private final Class<?> dataClass;

    private final Supplier<String> nameSupplier;

    public static ExportType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ExportType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
