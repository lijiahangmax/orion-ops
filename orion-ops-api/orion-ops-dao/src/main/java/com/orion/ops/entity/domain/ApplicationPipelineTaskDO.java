package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用流水线任务
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用流水线任务")
@TableName("application_pipeline_task")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水线id")
    @TableField("pipeline_id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线名称")
    @TableField("pipeline_name")
    private String pipelineName;

    @ApiModelProperty(value = "环境id")
    @TableField("profile_id")
    private Long profileId;

    @ApiModelProperty(value = "环境名称")
    @TableField("profile_name")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
    @TableField("profile_tag")
    private String profileTag;

    @ApiModelProperty(value = "执行标题")
    @TableField("exec_title")
    private String execTitle;

    @ApiModelProperty(value = "执行描述")
    @TableField("exec_description")
    private String execDescription;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    @TableField("exec_status")
    private Integer execStatus;

    @ApiModelProperty(value = "是否是定时执行 10普通执行 20定时执行")
    @TableField("timed_exec")
    private Integer timedExec;

    @ApiModelProperty(value = "定时执行时间")
    @TableField("timed_exec_time")
    private Date timedExecTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    @TableField("create_user_name")
    private String createUserName;

    @ApiModelProperty(value = "审核人id")
    @TableField("audit_user_id")
    private Long auditUserId;

    @ApiModelProperty(value = "审核人名称")
    @TableField("audit_user_name")
    private String auditUserName;

    @ApiModelProperty(value = "审核时间")
    @TableField("audit_time")
    private Date auditTime;

    @ApiModelProperty(value = "审核备注")
    @TableField("audit_reason")
    private String auditReason;

    @ApiModelProperty(value = "执行人id")
    @TableField("exec_user_id")
    private Long execUserId;

    @ApiModelProperty(value = "执行人名称")
    @TableField("exec_user_name")
    private String execUserName;

    @ApiModelProperty(value = "执行开始时间")
    @TableField("exec_start_time")
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    @TableField("exec_end_time")
    private Date execEndTime;

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
