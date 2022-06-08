package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线统计请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:10
 */
@Data
@ApiModel(value = "应用流水线统计请求")
public class AppPipelineTaskStatisticsRequest {

    @ApiModelProperty(value = "pipelineId")
    private Long pipelineId;

}
