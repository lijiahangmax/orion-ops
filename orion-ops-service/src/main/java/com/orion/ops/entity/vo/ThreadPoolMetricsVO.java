package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 线程池指标
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/23 18:17
 */
@Data
public class ThreadPoolMetricsVO {

    /**
     * type
     */
    private Integer type;

    /**
     * 活跃线程数
     */
    private Integer activeThreadCount;

    /**
     * 总任务数
     */
    private Long totalTaskCount;

    /**
     * 已完成任务数
     */
    private Long completedTaskCount;

    /**
     * 队列任务数
     */
    private Integer queueSize;

}
