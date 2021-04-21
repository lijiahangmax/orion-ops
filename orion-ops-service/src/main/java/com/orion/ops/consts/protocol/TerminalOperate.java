package com.orion.ops.consts.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 终端操作 operate
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 22:35
 */
@AllArgsConstructor
@Getter
public enum TerminalOperate {

    /**
     * 建立连接
     */
    CONNECT("connect"),

    /**
     * ping
     */
    PING("ping"),

    /**
     * 更改大小
     */
    RESIZE("resize"),

    /**
     * 键入key
     */
    KEY("key"),

    /**
     * 键入命令
     */
    COMMAND("command"),

    /**
     * 中断 ctrl+c
     */
    INTERRUPT("interrupt"),

    /**
     * 挂起 ctrl+x
     */
    HANGUP("hangup"),

    /**
     * 关闭连接
     */
    DISCONNECT("disconnect"),

    ;

    private String operate;

    public static TerminalOperate of(String operate) {
        for (TerminalOperate value : values()) {
            if (value.operate.equals(operate)) {
                return value;
            }
        }
        return null;
    }

}
