package com.orion.ops.consts.export;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    MACHINE(10),

    ;

    private final Integer type;


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
