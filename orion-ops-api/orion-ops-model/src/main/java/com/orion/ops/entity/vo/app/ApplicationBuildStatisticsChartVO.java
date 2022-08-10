package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 构建统计表格响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:42
 */
@Data
@ApiModel(value = "构建统计表格响应")
public class ApplicationBuildStatisticsChartVO {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "构建次数")
    private Integer buildCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

}
