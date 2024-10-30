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
package cn.orionsec.ops.constant.system;

import cn.orionsec.ops.constant.SchedulerPools;
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
     * terminal watcher
     */
    TERMINAL_WATCHER(15, SchedulerPools.TERMINAL_WATCHER_SCHEDULER),

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

    /**
     * 应用流水线
     */
    PIPELINE(130, SchedulerPools.PIPELINE_SCHEDULER),

    ;

    private final Integer type;

    private final ThreadPoolExecutor executor;

}
