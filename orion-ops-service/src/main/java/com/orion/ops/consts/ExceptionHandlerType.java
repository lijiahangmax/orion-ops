package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常处理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 10:02
 */
@Getter
@AllArgsConstructor
public enum ExceptionHandlerType {

    /**
     * 跳过所有 中断执行
     */
    SKIP_ALL(10, "skip_all"),

    /**
     * 跳过错误 继续执行
     */
    SKIP_ERROR(20, "skip_error"),

    ;

    private final Integer type;

    private final String value;

    public static ExceptionHandlerType of(Integer type) {
        if (type == null) {
            return ExceptionHandlerType.SKIP_ALL;
        }
        for (ExceptionHandlerType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return ExceptionHandlerType.SKIP_ALL;
    }

    public static ExceptionHandlerType of(String value) {
        if (value == null) {
            return ExceptionHandlerType.SKIP_ALL;
        }
        for (ExceptionHandlerType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return ExceptionHandlerType.SKIP_ALL;
    }

}
