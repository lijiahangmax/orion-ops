package com.orion.ops.consts.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件分类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/23 17:40
 */
@AllArgsConstructor
@Getter
public enum EventClassify {

    /**
     * 认证操作
     */
    AUTHENTICATION(5),

    /**
     * 机器操作
     */
    MACHINE(10),

    /**
     * 机器环境变量操作
     */
    MACHINE_ENV(15),

    /**
     * 秘钥操作
     */
    MACHINE_KEY(20),

    /**
     * 代理操作
     */
    MACHINE_PROXY(25),

    /**
     * 终端操作
     */
    TERMINAL(25),

    /**
     * sftp 操作
     */
    SFTP(30),

    ;

    /**
     * 分类
     */
    private final Integer classify;

    public static EventClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (EventClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }

}
