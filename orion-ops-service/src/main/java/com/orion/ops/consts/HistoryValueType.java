package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 历史值类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/3 14:00
 */
@AllArgsConstructor
@Getter
public enum HistoryValueType {

    /**
     * 机器环境变量
     */
    MACHINE_ENV(10),

    ;

    Integer type;

}
