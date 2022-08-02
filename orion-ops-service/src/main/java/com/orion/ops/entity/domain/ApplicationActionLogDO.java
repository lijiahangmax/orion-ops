package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用操作日志
 *
 * @author Jiahang Li
 * @since 2022-02-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用操作日志")
@TableName("application_action_log")
public class ApplicationActionLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    @TableField("stage_type")
    private Integer stageType;

    @ApiModelProperty(value = "引用id 构建id 发布机器id")
    @TableField("rel_id")
    private Long relId;

    @ApiModelProperty(value = "执行机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "操作id")
    @TableField("action_id")
    private Long actionId;

    @ApiModelProperty(value = "操作名称")
    @TableField("action_name")
    private String actionName;

    /**
     * @see com.orion.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "操作类型")
    @TableField("action_type")
    private Integer actionType;

    @ApiModelProperty(value = "操作命令")
    @TableField("action_command")
    private String actionCommand;

    @ApiModelProperty(value = "操作日志路径")
    @TableField("log_path")
    private String logPath;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    @TableField("run_status")
    private Integer runStatus;

    @ApiModelProperty(value = "退出码")
    @TableField("exit_code")
    private Integer exitCode;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
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
