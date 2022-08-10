package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 命令执行表
 *
 * @author Jiahang Li
 * @since 2021-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "命令执行表")
@TableName("command_exec")
@SuppressWarnings("ALL")
public class CommandExecDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    @TableField("user_name")
    private String userName;

    /**
     * @see com.orion.ops.constant.command.ExecType
     */
    @ApiModelProperty(value = "执行类型 10批量执行")
    @TableField("exec_type")
    private Integer execType;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    @TableField("machine_name")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    @TableField("machine_host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    @TableField("machine_tag")
    private String machineTag;

    /**
     * @see com.orion.ops.constant.command.ExecStatus
     */
    @ApiModelProperty(value = "执行状态 10未开始 20执行中 30执行成功 40执行失败 50执行终止")
    @TableField("exec_status")
    private Integer execStatus;

    @ApiModelProperty(value = "执行返回码")
    @TableField("exit_code")
    private Integer exitCode;

    @ApiModelProperty(value = "命令")
    @TableField("exec_command")
    private String execCommand;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "日志目录")
    @TableField("log_path")
    private String logPath;

    @ApiModelProperty(value = "执行开始时间")
    @TableField("start_date")
    private Date startDate;

    @ApiModelProperty(value = "执行结束时间")
    @TableField("end_date")
    private Date endDate;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
