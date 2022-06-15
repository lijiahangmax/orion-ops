package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用流水线统计分析操作响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:13
 */
@Data
@ApiModel(value = "应用流水线统计分析操作响应")
public class ApplicationPipelineTaskStatisticsViewVO {

    @ApiModelProperty(value = "成功平均执行时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均执行时长")
    private String avgUsedInterval;

    @ApiModelProperty(value = "流水线操作")
    private List<ApplicationPipelineStatisticsDetailVO> details;

    @ApiModelProperty(value = "流水线执行记录")
    private List<ApplicationPipelineTaskStatisticsTaskVO> pipelineTaskList;

}
