package com.orion.ops.handler.scheduler;

import org.springframework.scheduling.TaskScheduler;

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
    private Runnable runnable;

    /**
     * 执行时间
     */
    private Date time;

    /**
     * 异步执行
     */
    private volatile ScheduledFuture<?> future;

    public TimedTask(Runnable runnable, Date time) {
        this.runnable = runnable;
        this.time = time;
    }

    /**
     * 提交任务
     *
     * @param scheduler scheduler
     */
    public void submit(TaskScheduler scheduler) {
        this.future = scheduler.schedule(runnable, time);
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
