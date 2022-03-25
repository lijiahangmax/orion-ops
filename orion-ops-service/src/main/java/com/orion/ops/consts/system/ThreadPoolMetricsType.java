package com.orion.ops.consts.system;

import com.orion.ops.consts.SchedulerPools;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池指标类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/24 17:19
 */
@AllArgsConstructor
@Getter
public enum ThreadPoolMetricsType {

    /**
     * terminal
     */
    TERMINAL(10, SchedulerPools.TERMINAL_SCHEDULER),

    /**
     * 命令执行
     */
    EXEC(20, SchedulerPools.EXEC_SCHEDULER),

    /**
     * tail
     */
    TAIL(30, SchedulerPools.TAIL_SCHEDULER),

    /**
     * sftp 传输进度
     */
    SFTP_TRANSFER_RATE(40, SchedulerPools.SFTP_TRANSFER_RATE_SCHEDULER),

    /**
     * sftp 上传
     */
    SFTP_UPLOAD(50, SchedulerPools.SFTP_UPLOAD_SCHEDULER),

    /**
     * sftp 下载
     */
    SFTP_DOWNLOAD(60, SchedulerPools.SFTP_DOWNLOAD_SCHEDULER),

    /**
     * sftp 打包
     */
    SFTP_PACKAGE(70, SchedulerPools.SFTP_PACKAGE_SCHEDULER),

    /**
     * 应用构建
     */
    APP_BUILD(80, SchedulerPools.APP_BUILD_SCHEDULER),

    /**
     * 应用发布 主线程
     */
    RELEASE_MAIN(90, SchedulerPools.RELEASE_MAIN_SCHEDULER),

    /**
     * 应用发布 机器操作
     */
    RELEASE_MACHINE(100, SchedulerPools.RELEASE_MACHINE_SCHEDULER),

    /**
     * 调度任务 主线程
     */
    SCHEDULER_TASK_MAIN(110, SchedulerPools.SCHEDULER_TASK_MAIN_SCHEDULER),

    /**
     * 调度任务 机器操作
     */
    SCHEDULER_TASK_MACHINE(120, SchedulerPools.SCHEDULER_TASK_MACHINE_SCHEDULER),

    ;

    private final Integer type;

    private final ThreadPoolExecutor executor;

}
