package com.orion.ops.constant.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息分类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:35
 */
@AllArgsConstructor
@Getter
public enum MessageClassify {

    /**
     * 系统消息
     */
    SYSTEM(10, "系统消息"),

    /**
     * 数据导入
     */
    IMPORT(20, "系统消息"),

    /**
     * 报警信息
     */
    ALARM(30, "报警信息"),

    ;

    private final Integer classify;

    private final String label;

    public static MessageClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (MessageClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }

}
