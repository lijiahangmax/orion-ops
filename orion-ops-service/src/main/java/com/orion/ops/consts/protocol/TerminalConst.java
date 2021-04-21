package com.orion.ops.consts.protocol;

import com.orion.lang.thread.ExecutorBuilder;
import com.orion.ops.consts.Const;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;

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
     * 终端连接尝试次数
     */
    public static final int TERMINAL_CONNECT_RETRY_TIMES = 3;

    /**
     * 终端连接超时时间
     */
    public static final int TERMINAL_CONNECT_TIMEOUT = Const.MS_S_3;

    /**
     * 判断终端心跳断开的阀值
     */
    public static final int TERMINAL_CONNECT_DOWN = Const.MS_S_30 * 3;

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

    /**
     * terminal 调度线程池
     */
    public static final ExecutorService TERMINAL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("terminal-scheduler-thread-")
            .setCorePoolSize(2)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(com.orion.constant.Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

}
