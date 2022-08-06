package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器端点请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/6 14:48
 */
@Data
@ApiModel(value = "机器监控请求")
public class MachineMonitorEndpointRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "数据粒度")
    private Integer granularity;

    @ApiModelProperty(value = "开始区间 (秒)")
    private Long startRange;

    @ApiModelProperty(value = "结束区间 (秒)")
    private Long endRange;

    @ApiModelProperty(value = "磁盘序列")
    private String seq;
}
