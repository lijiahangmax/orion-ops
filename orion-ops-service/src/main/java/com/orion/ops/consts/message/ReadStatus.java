package com.orion.ops.consts.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:36
 */
@AllArgsConstructor
@Getter
public enum ReadStatus {

    /**
     * 未读
     */
    UNREAD(1),

    /**
     * 已读
     */
    READ(2),

    ;

    private final Integer status;

}
