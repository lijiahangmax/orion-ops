package com.orion.ops.entity.request.machine;

import com.orion.ops.entity.vo.machine.MachineAlarmConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器监控同步请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/1 14:49
 */
@Data
@ApiModel(value = "机器监控同步请求")
public class MachineMonitorSyncRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "报警配置")
    private List<MachineAlarmConfigVO> alarmConfig;

}
