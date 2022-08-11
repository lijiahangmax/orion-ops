package com.orion.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 线程池指标响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/23 18:17
 */
@Data
@ApiModel(value = "线程池指标响应")
@SuppressWarnings("ALL")
public class ThreadPoolMetricsVO {

    /**
     * @see com.orion.ops.constant.system.ThreadPoolMetricsType
     */
    @ApiModelProperty(value = "type")
    private Integer type;

    @ApiModelProperty(value = "活跃线程数")
    private Integer activeThreadCount;

    @ApiModelProperty(value = "总任务数")
    private Long totalTaskCount;

    @ApiModelProperty(value = "已完成任务数")
    private Long completedTaskCount;

    @ApiModelProperty(value = "队列任务数")
    private Integer queueSize;

}
