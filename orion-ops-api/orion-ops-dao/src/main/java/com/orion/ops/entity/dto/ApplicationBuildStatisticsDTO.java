package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用构建统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 15:35
 */
@Data
@ApiModel(value = "应用构建统计")
public class ApplicationBuildStatisticsDTO {

    @ApiModelProperty(value = "构建次数")
    private Integer buildCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "平均构建时长ms (成功)")
    private Long avgUsed;

}
