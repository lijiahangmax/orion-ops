package com.orion.ops.consts.export;

import com.orion.ops.consts.machine.MachineFieldConst;
import com.orion.ops.consts.machine.MachineProxyFieldConst;
import com.orion.ops.consts.tail.FileTailFieldConst;
import com.orion.ops.entity.dto.importer.MachineInfoImportDTO;
import com.orion.ops.entity.dto.importer.MachineProxyImportDTO;
import com.orion.ops.entity.dto.importer.MachineTailFileImportDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
            MachineFieldConst::validData),

    /**
     * 导出机器代理
     */
    MACHINE_PROXY(110,
            "/templates/machine-proxy-import-template.xlsx",
            "机器代理导入模板.xlsx",
            MachineProxyImportDTO.class,
            MachineProxyFieldConst::validData),

    /**
     * 导出日志文件
     */
    MACHINE_TAIL_FILE(130,
            "/templates/machine-tail-file-import-template.xlsx",
            "日志文件导入模板.xlsx",
            MachineTailFileImportDTO.class,
            FileTailFieldConst::validData),


    ;
    //     /**
    //      * 导出应用环境
    //      */
    //     PROFILE(200, () -> ExportConst.getFileName(ExportConst.APP_PROFILE_EXPORT_NAME)),
    //
    //     /**
    //      * 导出应用
    //      */
    //     APPLICATION(210, () -> ExportConst.getFileName(ExportConst.APPLICATION_EXPORT_NAME)),
    //
    //     /**
    //      * 应用仓库
    //      */
    //     VCS(220, () -> ExportConst.getFileName(ExportConst.APP_VCS_EXPORT_NAME)),
    //
    //     /**
    //      * 命令模板
    //      */
    //     COMMAND_TEMPLATE(310, () -> ExportConst.getFileName(ExportConst.COMMAND_TEMPLATE_EXPORT_NAME)),

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
    private Class<?> importClass;

    /**
     * 数据验证
     */
    private Consumer<Object> valid;

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
