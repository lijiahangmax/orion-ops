package com.orion.ops.entity.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页统计数量返回响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:08
 */
@Data
@ApiModel(value = "首页统计数量返回响应")
public class HomeStatisticsCountVO {

    @ApiModelProperty(value = "机器数量")
    private Integer machineCount;

    @ApiModelProperty(value = "环境数量")
    private Integer profileCount;

    @ApiModelProperty(value = "应用数量")
    private Integer appCount;

    @ApiModelProperty(value = "流水线数量")
    private Integer pipelineCount;

}
