package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用流水线统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 10:49
 */
@Data
@ApiModel(value = "应用流水线统计")
public class ApplicationPipelineTaskStatisticsDTO {

    @ApiModelProperty(value = "执行次数")
    private Integer execCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "平均执行时长ms (成功)")
    private Long avgUsed;

}
