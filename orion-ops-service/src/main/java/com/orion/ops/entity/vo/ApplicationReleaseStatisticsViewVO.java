package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用发布统计分析响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 18:05
 */
@Data
@ApiModel(value = "应用发布统计分析响应")
public class ApplicationReleaseStatisticsViewVO {

    @ApiModelProperty(value = "成功平均发布时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均发布时长")
    private String avgUsedInterval;

    @ApiModelProperty(value = "发布明细列表")
    private List<ApplicationReleaseStatisticsRecordVO> releaseRecordList;

    @ApiModelProperty(value = "发布操作")
    private List<ApplicationStatisticsActionVO> actions;

}
