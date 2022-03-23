package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 调度任务统计请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/22 17:12
 */
@Data
public class SchedulerTaskStatisticsRequest {

    /**
     * taskId
     */
    private Long taskId;

}
