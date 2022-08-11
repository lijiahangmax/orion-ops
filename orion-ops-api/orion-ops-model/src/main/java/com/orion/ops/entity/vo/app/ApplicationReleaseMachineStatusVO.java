package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 发布机器状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 21:52
 */
@Data
@ApiModel(value = "发布机器状态响应")
@SuppressWarnings("ALL")
public class ApplicationReleaseMachineStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "发布开始时间")
    private Date startTime;

    @ApiModelProperty(value = "发布开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "发布结束时间")
    private Date endTime;

    @ApiModelProperty(value = "发布结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "发布操作状态")
    private List<ApplicationActionStatusVO> actions;

}
