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
package cn.orionsec.ops.task;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时 任务包装器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 10:34
 */
public class TimedTask {

    /**
     * 任务
     */
    private final Runnable runnable;

    /**
     * 异步执行
     */
    private volatile ScheduledFuture<?> future;

    public TimedTask(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * 提交任务 一次性
     *
     * @param scheduler scheduler
     * @param time      time
     */
    public void submit(TaskScheduler scheduler, Date time) {
        this.future = scheduler.schedule(runnable, time);
    }

    /**
     * 提交任务 cron表达式
     *
     * @param trigger   trigger
     * @param scheduler scheduler
     */
    public void submit(TaskScheduler scheduler, Trigger trigger) {
        this.future = scheduler.schedule(runnable, trigger);
    }

    /**
     * 取消定时任务
     */
    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

}
