package com.orion.ops.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器监控查询参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/2 10:22
 */
@Data
@ApiModel(value = "机器监控查询参数")
public class MachineMonitorQuery {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see com.orion.ops.constant.monitor.InstallStatus
     */
    @ApiModelProperty(value = "插件安装状态 1未安装 2安装中 3已安装")
    private Integer installStatus;

}
