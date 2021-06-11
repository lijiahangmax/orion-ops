package com.orion.ops.consts;

import com.orion.lang.thread.ExecutorBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:04
 */
public class SchedulerPools {

    private SchedulerPools() {
    }

    /**
     * terminal 调度线程池
     */
    public static final ExecutorService TERMINAL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("terminal-scheduler-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(com.orion.constant.Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * 命令执行 调度线程池
     */
    public static final ExecutorService EXEC_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("exec-scheduler-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(com.orion.constant.Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * tail 调度线程池
     */
    public static final ExecutorService TAIL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("tail-scheduler-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(com.orion.constant.Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

}
