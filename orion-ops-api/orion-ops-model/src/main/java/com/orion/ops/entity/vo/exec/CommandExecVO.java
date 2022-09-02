package com.orion.ops.entity.vo.exec;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 命令执行响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 15:28
 */
@Data
@ApiModel(value = "命令执行响应")
@SuppressWarnings("ALL")
public class CommandExecVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "执行用户id")
    private Long userId;

    @ApiModelProperty(value = "执行用户名称")
    private String username;

    /**
     * @see com.orion.ops.constant.command.ExecType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    /**
     * @see com.orion.ops.constant.command.ExecStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "执行机器名称")
    private String machineName;

    @ApiModelProperty(value = "执行机器主机")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "执行退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "执行命令")
    private String command;

    @ApiModelProperty(value = "执行开始时间")
    private Date startDate;

    @ApiModelProperty(value = "执行开始时间")
    private String startDateAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endDate;

    @ApiModelProperty(value = "执行结束时间")
    private String endDateAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

}
