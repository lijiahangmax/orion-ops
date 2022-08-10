package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 17:52
 */
@Data
@ApiModel(value = "应用操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationActionLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    @ApiModelProperty(value = "操作id")
    private Long actionId;

    @ApiModelProperty(value = "操作名称")
    private String actionName;

    /**
     * @see com.orion.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "操作类型")
    private Integer actionType;

    @ApiModelProperty(value = "操作命令")
    private String actionCommand;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
