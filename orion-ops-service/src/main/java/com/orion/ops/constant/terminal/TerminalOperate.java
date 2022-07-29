package com.orion.ops.constant.terminal;

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
     * 键入
     */
    KEY("0", true),

    /**
     * 连接
     */
    CONNECT("1", true),

    /**
     * ping
     */
    PING("2", false),

    /**
     * 更改大小
     */
    RESIZE("3", true),

    /**
     * 键入命令
     */
    COMMAND("4", true),

    /**
     * ctrl + e
     */
    NOP("5", false),

    /**
     * 关闭连接
     */
    DISCONNECT("6", false),

    ;

    /**
     * 前缀长度
     */
    public static final int PREFIX_SIZE = 1;

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
