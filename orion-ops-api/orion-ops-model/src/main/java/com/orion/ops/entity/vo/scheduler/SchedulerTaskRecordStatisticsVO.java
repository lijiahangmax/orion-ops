package com.orion.ops.entity.vo.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 调度任务统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/21 10:14
 */
@Data
@ApiModel(value = "调度任务统计响应")
public class SchedulerTaskRecordStatisticsVO {

    @ApiModelProperty(value = "调度次数")
    private Integer scheduledCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功机器平均执行时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功机器平均执行时长")
    private String avgUsedInterval;

    @ApiModelProperty(value = "机器执行统计")
    private List<SchedulerTaskMachineRecordStatisticsVO> machineList;

    @ApiModelProperty(value = "调度图表")
    private List<SchedulerTaskRecordStatisticsChartVO> charts;

}
