package com.orion.ops.consts.export;

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
@AllArgsConstructor
@Getter
public enum ExportType {

    /**
     * 导出机器
     */
    MACHINE(100, () -> ExportConst.getFileName(ExportConst.MACHINE_EXPORT_NAME)),

    /**
     * 导出应用环境
     */
    PROFILE(200, () -> ExportConst.getFileName(ExportConst.APP_PROFILE_EXPORT_NAME)),

    /**
     * 导出应用
     */
    APPLICATION(210, () -> ExportConst.getFileName(ExportConst.APPLICATION_EXPORT_NAME)),

    /**
     * 应用仓库
     */
    VCS(220, () -> ExportConst.getFileName(ExportConst.APP_VCS_EXPORT_NAME)),

    ;

    private final Integer type;

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
