package com.orion.ops.constant;

import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.importer.*;
import com.orion.ops.validator.*;
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
     * 导入机器
     */
    MACHINE(100,
            "机器信息",
            "/templates/import/machine-import-template.xlsx",
            "机器导入模板.xlsx",
            MachineInfoImportDTO.class,
            MachineValidator.INSTANCE,
            MachineInfoDO.class),

    /**
     * 导入机器代理
     */
    MACHINE_PROXY(110,
            "机器代理",
            "/templates/import/machine-proxy-import-template.xlsx",
            "机器代理导入模板.xlsx",
            MachineProxyImportDTO.class,
            MachineProxyValidator.INSTANCE,
            MachineProxyDO.class),

    /**
     * 导入日志文件
     */
    MACHINE_TAIL_FILE(130,
            "日志文件",
            "/templates/import/tail-file-import-template.xlsx",
            "日志文件导入模板.xlsx",
            MachineTailFileImportDTO.class,
            FileTailValidator.INSTANCE,
            FileTailListDO.class),

    /**
     * 导入应用环境
     */
    PROFILE(200,
            "应用环境",
            "/templates/import/app-profile-import-template.xlsx",
            "应用环境导入模板.xlsx",
            ApplicationProfileImportDTO.class,
            ApplicationProfileValidator.INSTANCE,
            ApplicationProfileDO.class),

    /**
     * 导入应用
     */
    APPLICATION(210,
            "应用信息",
            "/templates/import/application-import-template.xlsx",
            "应用导入模板.xlsx",
            ApplicationImportDTO.class,
            ApplicationValidator.INSTANCE,
            ApplicationInfoDO.class),

    /**
     * 导入应用仓库
     */
    REPOSITORY(220,
            "版本仓库",
            "/templates/import/app-repository-import-template.xlsx",
            "应用仓库导入模板.xlsx",
            ApplicationRepositoryImportDTO.class,
            ApplicationRepositoryValidator.INSTANCE,
            ApplicationRepositoryDO.class),

    /**
     * 导入命令模板
     */
    COMMAND_TEMPLATE(310,
            "命令模板",
            "/templates/import/command-template-import-template.xlsx",
            "命令模板导入模板.xlsx",
            CommandTemplateImportDTO.class,
            CommandTemplateValidator.INSTANCE,
            CommandTemplateDO.class),

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
     * importClass
     */
    private final Class<? extends BaseDataImportDTO> importClass;

    /**
     * 数据验证器
     */
    private final DataValidator validator;

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
