package com.orion.ops.handler.app.machine;

/**
 * 机器执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 14:10
 */
public enum MachineProcessorStatus {

    /**
     * 未开始
     */
    WAIT,

    /**
     * 进行中
     */
    RUNNABLE,

    /**
     * 已完成
     */
    FINISH,

    /**
     * 执行失败
     */
    FAILURE,

    /**
     * 已跳过
     */
    SKIPPED,

    /**
     * 已终止
     */
    TERMINATED,

    ;

}
