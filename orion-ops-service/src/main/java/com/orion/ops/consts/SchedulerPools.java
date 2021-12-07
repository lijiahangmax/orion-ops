package com.orion.ops.consts;

import com.orion.lang.thread.ExecutorBuilder;
import com.orion.utils.Systems;
import com.orion.utils.Threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * tail 调度线程池
     */
    public static final ExecutorService TAIL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("tail-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();


    /**
     * sftp 上传线程池
     */
    public static final ExecutorService SFTP_UPLOAD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-upload-thread-")
            .setCorePoolSize(6)
            .setMaxPoolSize(6)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * sftp 下载线程池
     */
    public static final ExecutorService SFTP_DOWNLOAD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-download-thread-")
            .setCorePoolSize(6)
            .setMaxPoolSize(6)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * sftp 打包线程池
     */
    public static final ExecutorService SFTP_PACKAGE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-package-thread-")
            .setCorePoolSize(4)
            .setMaxPoolSize(4)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * 应用构建线程池
     */
    public static final ExecutorService APP_BUILD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("app-build-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * 发布单 主线程操作线程池
     */
    public static final ExecutorService RELEASE_MAIN_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("release-main-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    /**
     * 发布单 目标机器操作线程池
     */
    public static final ExecutorService RELEASE_TARGET_STAGE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("release-target-stage-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeOut(true)
            .build();

    static {
        Systems.addShutdownHook(() -> {
            Threads.shutdownPoolNow(TERMINAL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(EXEC_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(TAIL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_UPLOAD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_DOWNLOAD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_PACKAGE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(APP_BUILD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(RELEASE_MAIN_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(RELEASE_TARGET_STAGE_SCHEDULER, Const.MS_S_3);
        });
    }

}
