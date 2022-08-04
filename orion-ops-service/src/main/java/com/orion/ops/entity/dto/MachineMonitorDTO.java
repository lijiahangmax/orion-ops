package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 机器监控配置
 *
 * @author Jiahang Li
 * @since 2022-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器监控配置")
public class MachineMonitorDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    /**
     * @see com.orion.ops.constant.monitor.MonitorStatus
     */
    @ApiModelProperty(value = "监控状态 1未安装 2安装中 3未运行 4运行中")
    private Integer monitorStatus;

    @ApiModelProperty(value = "请求 api url")
    private String monitorUrl;

    @ApiModelProperty(value = "请求 api accessToken")
    private String accessToken;

    @ApiModelProperty(value = "插件版本")
    private String agentVersion;

}
