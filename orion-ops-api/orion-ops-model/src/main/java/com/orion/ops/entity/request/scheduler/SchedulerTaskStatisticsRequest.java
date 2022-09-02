package com.orion.ops.entity.request.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 调度任务统计请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/22 17:12
 */
@Data
@ApiModel(value = "调度任务统计请求")
public class SchedulerTaskStatisticsRequest {

    @ApiModelProperty(value = "taskId")
    private Long taskId;

}
