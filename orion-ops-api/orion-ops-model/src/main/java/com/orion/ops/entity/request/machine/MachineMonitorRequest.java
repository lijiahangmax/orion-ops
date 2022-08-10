package com.orion.ops.entity.request.machine;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器监控请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器监控请求")
@SuppressWarnings("ALL")
public class MachineMonitorRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see com.orion.ops.constant.monitor.MonitorStatus
     */
    @ApiModelProperty(value = "安装状态")
    private Integer status;

    @ApiModelProperty(value = "请求url")
    private String url;

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

}
