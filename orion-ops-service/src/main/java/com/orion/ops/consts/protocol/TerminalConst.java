package com.orion.ops.consts.protocol;

import com.orion.ops.consts.Const;

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

    public static final String TERMINAL = "terminal";

    /**
     * 终端token过期时间
     */
    public static final int TERMINAL_TOKEN_EXPIRE_S = 60 * 60;

    /**
     * 终端id缓存时长
     */
    public static final int TERMINAL_ID_EXPIRE_S = 60;

    /**
     * 判断终端心跳断开的阀值
     */
    public static final int TERMINAL_CONNECT_DOWN = Const.MS_S_60 + Const.MS_S_15;

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
    public static final int FONT_SIZE = 18;

}
