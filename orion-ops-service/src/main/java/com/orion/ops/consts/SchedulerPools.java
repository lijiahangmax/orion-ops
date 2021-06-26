package com.orion.ops.consts;

import com.orion.lang.thread.ExecutorBuilder;
import com.orion.utils.Systems;
import com.orion.utils.Threads;

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
            .setNamedThreadFactory("terminal-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * 命令执行 调度线程池
     */
    public static final ExecutorService EXEC_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("exec-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * tail 调度线程池
     */
    public static final ExecutorService TAIL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("tail-thread-")
            .setCorePoolSize(0)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * sftp 调度线程池
     */
    public static final ExecutorService SFTP_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-transfer-thread-")
            .setCorePoolSize(0)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    static {
        Systems.addShutdownHook(() -> {
            Threads.shutdownPoolNow(TERMINAL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(EXEC_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(TAIL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_SCHEDULER, Const.MS_S_3);
        });
    }

}
