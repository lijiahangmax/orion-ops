package com.orion.ops.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 调度任务机器执行统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/22 15:08
 */
@Data
public class SchedulerTaskRecordStatisticsDTO {

    /**
     * 调度次数
     */
    private Integer scheduledCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failureCount;

    /**
     * 日期
     */
    private Date date;

    /**
     * 机器平均执行时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

}
