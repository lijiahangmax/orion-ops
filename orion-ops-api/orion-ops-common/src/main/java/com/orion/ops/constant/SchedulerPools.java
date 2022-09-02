package com.orion.ops.constant;

import com.orion.lang.define.thread.ExecutorBuilder;
import com.orion.lang.utils.Systems;
import com.orion.lang.utils.Threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

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
    public static final ThreadPoolExecutor TERMINAL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("terminal-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * terminal watcher 调度线程池
     */
    public static final ThreadPoolExecutor TERMINAL_WATCHER_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("terminal-watcher-thread-")
            .setCorePoolSize(0)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 命令执行 调度线程池
     */
    public static final ThreadPoolExecutor EXEC_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("exec-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * tail 调度线程池
     */
    public static final ThreadPoolExecutor TAIL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("tail-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();


    /**
     * sftp 传输进度线程池
     */
    public static final ThreadPoolExecutor SFTP_TRANSFER_RATE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-transfer-rate-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();


    /**
     * sftp 上传线程池
     */
    public static final ThreadPoolExecutor SFTP_UPLOAD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-upload-thread-")
            .setCorePoolSize(6)
            .setMaxPoolSize(6)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * sftp 下载线程池
     */
    public static final ThreadPoolExecutor SFTP_DOWNLOAD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-download-thread-")
            .setCorePoolSize(6)
            .setMaxPoolSize(6)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * sftp 打包线程池
     */
    public static final ThreadPoolExecutor SFTP_PACKAGE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("sftp-package-thread-")
            .setCorePoolSize(4)
            .setMaxPoolSize(4)
            .setKeepAliveTime(Const.MS_S_60)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 应用构建线程池
     */
    public static final ThreadPoolExecutor APP_BUILD_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("app-build-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 应用发布 主线程操作线程池
     */
    public static final ThreadPoolExecutor RELEASE_MAIN_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("release-main-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 应用发布 机器操作线程池
     */
    public static final ThreadPoolExecutor RELEASE_MACHINE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("release-machine-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 调度任务 主进程操作线程池
     */
    public static final ThreadPoolExecutor SCHEDULER_TASK_MAIN_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("scheduler-task-main-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 调度任务 机器操作线程池
     */
    public static final ThreadPoolExecutor SCHEDULER_TASK_MACHINE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("scheduler-task-machine-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 应用流水线 线程池
     */
    public static final ThreadPoolExecutor PIPELINE_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("pipeline-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 异步导入 线程池
     */
    public static final ThreadPoolExecutor ASYNC_IMPORT_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("async-import-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(4)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 插件安装 线程池
     */
    public static final ThreadPoolExecutor AGENT_INSTALL_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("agent-install-thread-")
            .setCorePoolSize(4)
            .setMaxPoolSize(4)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new LinkedBlockingQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();

    /**
     * 机器报警 线程池
     */
    public static final ThreadPoolExecutor MACHINE_ALARM_SCHEDULER = ExecutorBuilder.create()
            .setNamedThreadFactory("machine-alarm-thread-")
            .setCorePoolSize(1)
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setKeepAliveTime(Const.MS_S_30)
            .setWorkQueue(new SynchronousQueue<>())
            .setAllowCoreThreadTimeout(true)
            .build();


    static {
        Systems.addShutdownHook(() -> {
            Threads.shutdownPoolNow(TERMINAL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(TERMINAL_WATCHER_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(EXEC_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(TAIL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_TRANSFER_RATE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_UPLOAD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_DOWNLOAD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SFTP_PACKAGE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(APP_BUILD_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(RELEASE_MAIN_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(RELEASE_MACHINE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SCHEDULER_TASK_MAIN_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(SCHEDULER_TASK_MACHINE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(PIPELINE_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(ASYNC_IMPORT_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(AGENT_INSTALL_SCHEDULER, Const.MS_S_3);
            Threads.shutdownPoolNow(MACHINE_ALARM_SCHEDULER, Const.MS_S_3);
        });
    }

}
