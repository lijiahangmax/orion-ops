package com.orion.ops.handler.scheduler.machine;

import com.orion.able.SafeCloseable;
import com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus;

/**
 * 机器处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 21:15
 */
public interface ITaskMachineHandler extends Runnable, SafeCloseable {

    /**
     * 跳过 (未开始)
     */
    void skipped();

    /**
     * 停止 (进行中)
     */
    void terminated();

    /**
     * 状态
     *
     * @return 获取状态
     */
    SchedulerTaskMachineStatus getStatus();

}
