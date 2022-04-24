package com.orion.ops.consts;

/**
 * 高亮颜色
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/20 23:16
 */
public class StainCode {

    private StainCode() {
    }

    /**
     * 结束
     */
    public static String SUFFIX = (char) 27 + "[0m";

    // -------------------- 颜色 --------------------

    /**
     * 黑色
     */
    public static final int BLACK = 30;

    /**
     * 红色
     */
    public static final int RED = 31;

    /**
     * 绿色
     */
    public static final int GREEN = 32;

    /**
     * 黄色
     */
    public static final int YELLOW = 33;

    /**
     * 蓝色
     */
    public static final int BLUE = 34;

    /**
     * 紫色
     */
    public static final int PURPLE = 35;

    /**
     * 青色
     */
    public static final int CYAN = 36;

    /**
     * 白色
     */
    public static final int WHITE = 37;

    // -------------------- 背景色 --------------------

    /**
     * 黑色 背景色
     */
    public static final int BG_BLACK = 40;

    /**
     * 红色 背景色
     */
    public static final int BG_RED = 41;

    /**
     * 绿色 背景色
     */
    public static final int BG_GREEN = 42;

    /**
     * 黄色 背景色
     */
    public static final int BG_YELLOW = 43;

    /**
     * 蓝色 背景色
     */
    public static final int BG_BLUE = 44;

    /**
     * 紫色 背景色
     */
    public static final int BG_PURPLE = 45;

    /**
     * 青色 背景色
     */
    public static final int BG_CYAN = 46;

    /**
     * 白色 背景色
     */
    public static final int BG_WHITE = 47;

    // -------------------- 亮色 --------------------

    /**
     * 亮黑色 (灰)
     */
    public static final int GLOSS_BLACK = 90;

    /**
     * 亮红色
     */
    public static final int GLOSS_RED = 91;

    /**
     * 亮绿色
     */
    public static final int GLOSS_GREEN = 92;

    /**
     * 亮黄色
     */
    public static final int GLOSS_YELLOW = 93;

    /**
     * 亮蓝色
     */
    public static final int GLOSS_BLUE = 94;

    /**
     * 亮紫色
     */
    public static final int GLOSS_PURPLE = 95;

    /**
     * 亮青色
     */
    public static final int GLOSS_CYAN = 96;

    /**
     * 白色
     */
    public static final int GLOSS_WHITE = 97;

    // -------------------- 亮背景色 --------------------

    /**
     * 亮黑色 (灰) 背景色
     */
    public static final int BG_GLOSS_BLACK = 100;

    /**
     * 亮红色 背景色
     */
    public static final int BG_GLOSS_RED = 101;

    /**
     * 亮绿色 背景色
     */
    public static final int BG_GLOSS_GREEN = 102;

    /**
     * 亮黄色 背景色
     */
    public static final int BG_GLOSS_YELLOW = 103;

    /**
     * 亮蓝色 背景色
     */
    public static final int BG_GLOSS_BLUE = 104;

    /**
     * 亮紫色 背景色
     */
    public static final int BG_GLOSS_PURPLE = 105;

    /**
     * 亮青色 背景色
     */
    public static final int BG_GLOSS_CYAN = 106;

    /**
     * 白色 背景色
     */
    public static final int BG_GLOSS_WHITE = 107;

    /**
     * 获取颜色前缀
     * \x1b[31m
     *
     * @param code code
     * @return 前缀
     */
    public static String prefix(int code) {
        return (char) 27 + "[" + code + "m";
    }

}
