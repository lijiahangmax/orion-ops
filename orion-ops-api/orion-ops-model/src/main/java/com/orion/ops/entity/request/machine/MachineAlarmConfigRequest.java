package com.orion.ops.entity.request.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器报警配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 18:24
 */
@Data
@ApiModel(value = "机器报警配置请求")
@SuppressWarnings("ALL")
public class MachineAlarmConfigRequest {

    @ApiModelProperty(value = "machineId")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    private Integer type;

    @ApiModelProperty(value = "报警阈值")
    private Double alarmThreshold;

    @ApiModelProperty(value = "触发报警阈值 次")
    private Integer triggerThreshold;

    @ApiModelProperty(value = "报警通知沉默时间 分")
    private Integer notifySilence;

    @ApiModelProperty(value = "报警组id")
    private List<Long> groupIdList;

}
