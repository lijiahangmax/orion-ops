package com.orion.ops.entity.vo.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 调度机器执行状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 17:23
 */
@Data
@ApiModel(value = "调度机器执行状态响应")
@SuppressWarnings("ALL")
public class SchedulerTaskMachineRecordStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "执行记录id")
    private Long recordId;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskMachineStatus
     */
    @ApiModelProperty(value = "执行状态")
    private Integer status;

    @ApiModelProperty(value = "执行开始时间")
    private Date startTime;

    @ApiModelProperty(value = "执行开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "exitCode")
    private Integer exitCode;

}
