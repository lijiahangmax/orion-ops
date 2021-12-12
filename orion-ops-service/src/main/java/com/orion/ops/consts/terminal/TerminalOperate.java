package com.orion.ops.consts.terminal;

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
    CONNECT("05", true),

    /**
     * ping
     */
    PING("10", false),

    /**
     * 更改大小
     */
    RESIZE("15", true),

    /**
     * 键入key
     */
    KEY("20", true),

    /**
     * 键入命令
     */
    COMMAND("25", true),

    /**
     * 中断 ctrl+c
     */
    INTERRUPT("30", false),

    /**
     * 挂起 ctrl+x
     */
    HANGUP("35", false),

    /**
     * 关闭连接
     */
    DISCONNECT("40", false),

    ;

    /**
     * 前缀长度
     */
    public static final int PREFIX_SIZE = 2;

    private final String operate;

    private final boolean hasBody;

    public static TerminalOperate of(String operate) {
        for (TerminalOperate value : values()) {
            if (value.operate.equals(operate)) {
                return value;
            }
        }
        return null;
    }

}
