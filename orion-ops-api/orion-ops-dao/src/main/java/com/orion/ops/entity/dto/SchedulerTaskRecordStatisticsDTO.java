package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "调度任务机器执行统计")
public class SchedulerTaskRecordStatisticsDTO {

    @ApiModelProperty(value = "调度次数")
    private Integer scheduledCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "机器平均执行时长ms (成功)")
    private Long avgUsed;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

}
