package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用发布统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:19
 */
@Data
@ApiModel(value = "应用发布统计")
public class ApplicationReleaseStatisticsDTO {

    @ApiModelProperty(value = "发布次数")
    private Integer releaseCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "平均发布时长ms (成功)")
    private Long avgUsed;

}
