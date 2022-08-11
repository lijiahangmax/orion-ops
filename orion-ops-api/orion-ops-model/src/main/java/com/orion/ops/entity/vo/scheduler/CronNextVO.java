package com.orion.ops.entity.vo.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * cron下次执行时间响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/23 10:48
 */
@Data
@ApiModel(value = "cron下次执行时间响应")
public class CronNextVO {

    @ApiModelProperty(value = "是否有效")
    private Boolean valid;

    @ApiModelProperty(value = "下次执行时间")
    private List<Date> next;

}
