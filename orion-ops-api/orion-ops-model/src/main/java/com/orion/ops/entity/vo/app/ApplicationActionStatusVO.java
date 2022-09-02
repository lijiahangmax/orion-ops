package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用执行操作状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 17:00
 */
@Data
@ApiModel(value = "应用执行操作状态响应")
@SuppressWarnings("ALL")
public class ApplicationActionStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "执行开始时间")
    private Date startTime;

    @ApiModelProperty(value = "执行开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "exitCode")
    private Integer exitCode;

}
