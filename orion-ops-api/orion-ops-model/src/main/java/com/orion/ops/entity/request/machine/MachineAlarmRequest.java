package com.orion.ops.entity.request.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器报警请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 16:59
 */
@Data
@ApiModel(value = "机器报警请求")
@SuppressWarnings("ALL")
public class MachineAlarmRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    private Integer type;

    @ApiModelProperty(value = "报警值")
    private Double alarmValue;

    @ApiModelProperty(value = "报警时间")
    private Date alarmTime;

}
