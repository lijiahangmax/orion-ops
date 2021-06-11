package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模板类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 18:32
 */
@AllArgsConstructor
@Getter
public enum TemplateType {

    /**
     * 批量执行
     */
    BATCH_EXEC(10),

    ;

    Integer type;

    public static TemplateType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TemplateType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
