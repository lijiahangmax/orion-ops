package com.orion.ops.consts.export;

import com.orion.ops.consts.app.ApplicationFieldConst;
import com.orion.ops.consts.app.ApplicationProfileFieldConst;
import com.orion.ops.consts.app.ApplicationVcsFieldConst;
import com.orion.ops.consts.command.CommandTemplateFieldConst;
import com.orion.ops.consts.machine.MachineFieldConst;
import com.orion.ops.consts.machine.MachineProxyFieldConst;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.consts.tail.FileTailFieldConst;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.importer.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.function.Consumer;

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
            "/templates/machine-import-template.xlsx",
            "机器导入模板.xlsx",
            MachineInfoImportDTO.class,
            MachineFieldConst::validData,
            MachineInfoDO.class,
            MessageType.MACHINE_IMPORT_SUCCESS,
            MessageType.MACHINE_IMPORT_FAILURE),

    /**
     * 导入机器代理
     */
    MACHINE_PROXY(110,
            "/templates/machine-proxy-import-template.xlsx",
            "机器代理导入模板.xlsx",
            MachineProxyImportDTO.class,
            MachineProxyFieldConst::validData,
            MachineProxyDO.class,
            MessageType.MACHINE_PROXY_IMPORT_SUCCESS,
            MessageType.MACHINE_PROXY_IMPORT_FAILURE),

    /**
     * 导入日志文件
     */
    MACHINE_TAIL_FILE(130,
            "/templates/tail-file-import-template.xlsx",
            "日志文件导入模板.xlsx",
            MachineTailFileImportDTO.class,
            FileTailFieldConst::validData,
            FileTailListDO.class,
            MessageType.MACHINE_TAIL_FILE_IMPORT_SUCCESS,
            MessageType.MACHINE_TAIL_FILE_IMPORT_FAILURE),

    /**
     * 导入应用环境
     */
    PROFILE(200,
            "/templates/app-profile-import-template.xlsx",
            "应用环境导入模板.xlsx",
            ApplicationProfileImportDTO.class,
            ApplicationProfileFieldConst::validData,
            ApplicationProfileDO.class,
            MessageType.APP_PROFILE_IMPORT_SUCCESS,
            MessageType.APP_PROFILE_IMPORT_FAILURE),

    /**
     * 导入应用
     */
    APPLICATION(210,
            "/templates/application-import-template.xlsx",
            "应用导入模板.xlsx",
            ApplicationImportDTO.class,
            ApplicationFieldConst::validData,
            ApplicationInfoDO.class,
            MessageType.APPLICATION_IMPORT_SUCCESS,
            MessageType.APPLICATION_IMPORT_FAILURE),

    /**
     * 导入应用仓库
     */
    VCS(220,
            "/templates/app-vcs-import-template.xlsx",
            "应用仓库导入模板.xlsx",
            ApplicationVcsImportDTO.class,
            ApplicationVcsFieldConst::validData,
            ApplicationVcsDO.class,
            MessageType.APP_VCS_IMPORT_SUCCESS,
            MessageType.APP_VCS_IMPORT_FAILURE),

    /**
     * 导入命令模板
     */
    COMMAND_TEMPLATE(310,
            "/templates/command-template-import-template.xlsx",
            "命令模板导入模板.xlsx",
            CommandTemplateImportDTO.class,
            CommandTemplateFieldConst::validData,
            CommandTemplateDO.class,
            MessageType.COMMAND_TEMPLATE_IMPORT_SUCCESS,
            MessageType.COMMAND_TEMPLATE_IMPORT_FAILURE),

    ;

    /**
     * 类型
     */
    private final Integer type;

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
    private Class<? extends BaseDataImportDTO> importClass;

    /**
     * 数据验证器
     */
    private Consumer<Object> valid;

    private Class<? extends Serializable> convertClass;

    private MessageType successType;

    private MessageType failureType;

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
