package com.orion.ops.consts.message;

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
    SYSTEM(10),

    /**
     * 数据导入
     */
    IMPORT(20),

    ;

    private final Integer classify;

}
