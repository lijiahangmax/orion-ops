package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 命令块类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/6 18:57
 */
@AllArgsConstructor
@Getter
public enum ActionType {

    /**
     * 建立连接
     */
    CONNECT(10),

    /**
     * 检出代码
     */
    CHECKOUT(20),

    /**
     * 传输产物
     */
    TRANSFER(30),

    /**
     * 主机命令
     */
    HOST_COMMAND(40),

    /**
     * 目标机器命令
     */
    TARGET_COMMAND(50),

    ;

    Integer type;

    public static ActionType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ActionType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
