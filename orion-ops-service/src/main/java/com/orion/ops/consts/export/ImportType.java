package com.orion.ops.consts.export;

import com.orion.ops.consts.machine.MachineFieldConst;
import com.orion.ops.entity.dto.importer.MachineInfoImportDTO;
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
    MACHINE(10,
            "/templates/machine-import-template.xlsx",
            "机器导入模板.xlsx",
            MachineInfoImportDTO.class,
            MachineFieldConst::validData);

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
