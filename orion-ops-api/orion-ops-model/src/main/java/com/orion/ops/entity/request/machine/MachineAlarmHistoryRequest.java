package com.orion.ops.entity.request.machine;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 机器报警历史请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/31 10:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器报警历史请求")
@SuppressWarnings("ALL")
public class MachineAlarmHistoryRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "machineId")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    private Integer type;

    @ApiModelProperty(value = "报警值区间开始")
    private Double alarmValueStart;

    @ApiModelProperty(value = "报警值区间结束")
    private Double alarmValueEnd;

    @ApiModelProperty(value = "报警时间区间开始")
    private Date alarmTimeStart;

    @ApiModelProperty(value = "报警时间区间结束")
    private Date alarmTimeEnd;

}
