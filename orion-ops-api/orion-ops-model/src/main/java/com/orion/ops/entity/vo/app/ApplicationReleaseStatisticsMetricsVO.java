package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发布统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:17
 */
@Data
@ApiModel(value = "发布统计响应")
public class ApplicationReleaseStatisticsMetricsVO {

    @ApiModelProperty(value = "发布次数")
    private Integer releaseCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功平均发布时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均发布时长")
    private String avgUsedInterval;

}
