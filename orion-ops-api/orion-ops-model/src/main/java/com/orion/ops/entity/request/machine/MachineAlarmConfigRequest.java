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
public class MachineAlarmConfigRequest {

    @ApiModelProperty(value = "machineId")
    private Long machineId;

    @ApiModelProperty(value = "报警配置")
    private List<MachineAlarmConfigMetricsRequest> config;

    @ApiModelProperty(value = "报警组id")
    private List<Long> groupIdList;

}
