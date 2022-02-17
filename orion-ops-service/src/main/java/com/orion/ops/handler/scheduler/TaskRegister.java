package com.orion.ops.handler.scheduler;

import com.orion.ops.consts.MessageConst;
import com.orion.utils.Exceptions;
import com.orion.utils.collect.Maps;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 任务注册器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 10:46
 */
@Component
public class TaskRegister implements DisposableBean {

    private final Map<String, TimedTask> taskMap = Maps.newCurrentHashMap();

    @Resource
    @Qualifier("taskScheduler")
    private TaskScheduler scheduler;

    /**
     * 提交任务
     *
     * @param type   type
     * @param time   time
     * @param params params
     */
    public void submit(TaskType type, Date time, Object params) {
        // 生成任务
        Runnable runnable = type.create(params);
        String key = type.getKey(params);
        // 判断是否存在任务
        if (taskMap.containsKey(key)) {
            throw Exceptions.init(MessageConst.TASK_PRESENT);
        }
        TimedTask timedTask = new TimedTask(runnable, time);
        taskMap.put(key, timedTask);
        // 执行任务
        timedTask.submit(scheduler);
    }

    /**
     * 取消任务
     *
     * @param type   type
     * @param params params
     */
    public void cancel(TaskType type, Object params) {
        String key = type.getKey(params);
        TimedTask task = taskMap.get(key);
        if (task != null) {
            taskMap.remove(key);
            task.cancel();
        }
    }

    /**
     * 是否存在
     *
     * @param type   type
     * @param params params
     */
    public boolean has(TaskType type, Object params) {
        return taskMap.containsKey(type.getKey(params));
    }

    @Override
    public void destroy() {
        taskMap.values().forEach(TimedTask::cancel);
        taskMap.clear();
    }

}
