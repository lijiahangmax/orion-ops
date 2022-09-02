package com.orion.ops.constant.terminal;

import com.orion.ops.constant.Const;

/**
 * 终端常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 23:09
 */
public class TerminalConst {

    private TerminalConst() {
    }

    /**
     * 判断终端心跳断开的阀值
     */
    public static final int TERMINAL_CONNECT_DOWN = Const.MS_S_60 + Const.MS_S_15;

    /**
     * terminal symbol
     */
    public static final String TERMINAL = "terminal";

    /**
     * 默认背景色
     */
    public static final String BACKGROUND_COLOR = "#212529";

    /**
     * 默认字体颜色
     */
    public static final String FONT_COLOR = "#FFFFFF";

    /**
     * 默认字体大小
     */
    public static final int FONT_SIZE = 14;

    /**
     * 默认字体名称
     */
    public static final String FONT_FAMILY = "courier-new, courier, monospace";

}
