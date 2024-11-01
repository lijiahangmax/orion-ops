/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.constant;

import cn.orionsec.kit.lang.define.thread.ExecutorBuilder;
import cn.orionsec.kit.lang.utils.Systems;
import cn.orionsec.kit.lang.utils.Threads;

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
            .namedThreadFactory("terminal-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_60)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * terminal watcher 调度线程池
     */
    public static final ThreadPoolExecutor TERMINAL_WATCHER_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("terminal-watcher-thread-")
            .corePoolSize(0)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 命令执行 调度线程池
     */
    public static final ThreadPoolExecutor EXEC_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("exec-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * tail 调度线程池
     */
    public static final ThreadPoolExecutor TAIL_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("tail-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_60)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();


    /**
     * sftp 传输进度线程池
     */
    public static final ThreadPoolExecutor SFTP_TRANSFER_RATE_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("sftp-transfer-rate-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();


    /**
     * sftp 上传线程池
     */
    public static final ThreadPoolExecutor SFTP_UPLOAD_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("sftp-upload-thread-")
            .corePoolSize(6)
            .maxPoolSize(6)
            .keepAliveTime(Const.MS_S_60)
            .workQueue(new LinkedBlockingQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * sftp 下载线程池
     */
    public static final ThreadPoolExecutor SFTP_DOWNLOAD_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("sftp-download-thread-")
            .corePoolSize(6)
            .maxPoolSize(6)
            .keepAliveTime(Const.MS_S_60)
            .workQueue(new LinkedBlockingQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * sftp 打包线程池
     */
    public static final ThreadPoolExecutor SFTP_PACKAGE_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("sftp-package-thread-")
            .corePoolSize(4)
            .maxPoolSize(4)
            .keepAliveTime(Const.MS_S_60)
            .workQueue(new LinkedBlockingQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 应用构建线程池
     */
    public static final ThreadPoolExecutor APP_BUILD_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("app-build-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 应用发布 主线程操作线程池
     */
    public static final ThreadPoolExecutor RELEASE_MAIN_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("release-main-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 应用发布 机器操作线程池
     */
    public static final ThreadPoolExecutor RELEASE_MACHINE_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("release-machine-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 调度任务 主进程操作线程池
     */
    public static final ThreadPoolExecutor SCHEDULER_TASK_MAIN_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("scheduler-task-main-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 调度任务 机器操作线程池
     */
    public static final ThreadPoolExecutor SCHEDULER_TASK_MACHINE_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("scheduler-task-machine-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 应用流水线 线程池
     */
    public static final ThreadPoolExecutor PIPELINE_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("pipeline-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 异步导入 线程池
     */
    public static final ThreadPoolExecutor ASYNC_IMPORT_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("async-import-thread-")
            .corePoolSize(1)
            .maxPoolSize(4)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new LinkedBlockingQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 插件安装 线程池
     */
    public static final ThreadPoolExecutor AGENT_INSTALL_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("agent-install-thread-")
            .corePoolSize(4)
            .maxPoolSize(4)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new LinkedBlockingQueue<>())
            .allowCoreThreadTimeout(true)
            .build();

    /**
     * 机器报警 线程池
     */
    public static final ThreadPoolExecutor MACHINE_ALARM_SCHEDULER = ExecutorBuilder.create()
            .namedThreadFactory("machine-alarm-thread-")
            .corePoolSize(1)
            .maxPoolSize(Integer.MAX_VALUE)
            .keepAliveTime(Const.MS_S_30)
            .workQueue(new SynchronousQueue<>())
            .allowCoreThreadTimeout(true)
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
