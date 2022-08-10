package com.orion.ops.entity.request.scheduler;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 任务执行记录请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务执行记录请求")
@SuppressWarnings("ALL")
public class SchedulerTaskRecordRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "明细id")
    private Long recordId;

    @ApiModelProperty(value = "机器明细id")
    private Long machineRecordId;

    @ApiModelProperty(value = "机器明细id")
    private List<Long> machineRecordIdList;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "命令")
    private String command;

}
