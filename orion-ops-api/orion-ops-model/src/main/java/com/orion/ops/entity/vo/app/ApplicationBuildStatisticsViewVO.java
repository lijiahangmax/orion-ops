package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用构建统计分析操作响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:02
 */
@Data
@ApiModel(value = "应用构建统计分析操作响应")
public class ApplicationBuildStatisticsViewVO {

    @ApiModelProperty(value = "成功平均构建时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均构建时长")
    private String avgUsedInterval;

    @ApiModelProperty(value = "构建操作")
    private List<ApplicationActionStatisticsVO> actions;

    @ApiModelProperty(value = "构建记录")
    private List<ApplicationBuildStatisticsRecordVO> buildRecordList;

}
