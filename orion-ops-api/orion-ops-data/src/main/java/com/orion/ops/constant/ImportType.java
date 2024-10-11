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
package com.orion.ops.constant;

import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.importer.*;
import com.orion.ops.handler.importer.validator.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 导入类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:15
 */
@Getter
@AllArgsConstructor
public enum ImportType {

    /**
     * 机器信息
     */
    MACHINE_INFO(100,
            "机器信息",
            "/templates/import/machine-import-template.xlsx",
            "机器导入模板.xlsx",
            MachineValidator.INSTANCE,
            MachineInfoImportDTO.class,
            MachineInfoDO.class),

    /**
     * 机器代理
     */
    MACHINE_PROXY(110,
            "机器代理",
            "/templates/import/machine-proxy-import-template.xlsx",
            "机器代理导入模板.xlsx",
            MachineProxyValidator.INSTANCE,
            MachineProxyImportDTO.class,
            MachineProxyDO.class),

    /**
     * 日志文件
     */
    TAIL_FILE(120,
            "日志文件",
            "/templates/import/tail-file-import-template.xlsx",
            "日志文件导入模板.xlsx",
            FileTailValidator.INSTANCE,
            MachineTailFileImportDTO.class,
            FileTailListDO.class),

    /**
     * 应用环境
     */
    APP_PROFILE(200,
            "应用环境",
            "/templates/import/app-profile-import-template.xlsx",
            "应用环境导入模板.xlsx",
            ApplicationProfileValidator.INSTANCE,
            ApplicationProfileImportDTO.class,
            ApplicationProfileDO.class),

    /**
     * 应用信息
     */
    APPLICATION(210,
            "应用信息",
            "/templates/import/application-import-template.xlsx",
            "应用导入模板.xlsx",
            ApplicationValidator.INSTANCE,
            ApplicationImportDTO.class,
            ApplicationInfoDO.class),

    /**
     * 应用仓库
     */
    APP_REPOSITORY(220,
            "版本仓库",
            "/templates/import/app-repository-import-template.xlsx",
            "应用仓库导入模板.xlsx",
            ApplicationRepositoryValidator.INSTANCE,
            ApplicationRepositoryImportDTO.class,
            ApplicationRepositoryDO.class),

    /**
     * 命令模板
     */
    COMMAND_TEMPLATE(300,
            "命令模板",
            "/templates/import/command-template-import-template.xlsx",
            "命令模板导入模板.xlsx",
            CommandTemplateValidator.INSTANCE,
            CommandTemplateImportDTO.class,
            CommandTemplateDO.class),

    /**
     * webhook
     */
    WEBHOOK(310,
            "webhook",
            "/templates/import/webhook-import-template.xlsx",
            "webhook导入模板.xlsx",
            WebhookValidator.INSTANCE,
            WebhookImportDTO.class,
            WebhookConfigDO.class),

    ;

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 导入标签
     */
    private final String label;

    /**
     * 文件路径
     */
    private final String templatePath;

    /**
     * 下载名称
     */
    private final String templateName;

    /**
     * 数据验证器
     */
    private final DataValidator validator;

    /**
     * importClass
     */
    private final Class<? extends BaseDataImportDTO> importClass;

    private final Class<? extends Serializable> convertClass;

    public static ImportType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ImportType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
