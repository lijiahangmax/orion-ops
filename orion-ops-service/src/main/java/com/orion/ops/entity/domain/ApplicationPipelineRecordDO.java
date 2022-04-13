package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用流水线执行明细
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_pipeline_record")
public class ApplicationPipelineRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水线id
     */
    @TableField("pipeline_id")
    private Long pipelineId;

    /**
     * 流水线名称
     */
    @TableField("pipeline_name")
    private String pipelineName;

    /**
     * 环境id
     */
    @TableField("profile_id")
    private Long profileId;

    /**
     * 环境名称
     */
    @TableField("profile_name")
    private String profileName;

    /**
     * 环境tag
     */
    @TableField("profile_tag")
    private String profileTag;

    /**
     * 执行标题
     */
    @TableField("exec_title")
    private String execTitle;

    /**
     * 执行描述
     */
    @TableField("exec_description")
    private String execDescription;

    /**
     * 执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败
     *
     * @see com.orion.ops.consts.app.PipelineStatus
     */
    @TableField("exec_status")
    private Integer execStatus;

    /**
     * 是否是定时执行 10普通执行 20定时执行
     */
    @TableField("timed_exec")
    private Integer timedExec;

    /**
     * 定时执行时间
     */
    @TableField("timed_exec_time")
    private Date timedExecTime;

    /**
     * 创建人id
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 审核人id
     */
    @TableField("audit_user_id")
    private Long auditUserId;

    /**
     * 审核人名称
     */
    @TableField("audit_user_name")
    private String auditUserName;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private Date auditTime;

    /**
     * 审核备注
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 执行人id
     */
    @TableField("exec_user_id")
    private Long execUserId;

    /**
     * 执行人名称
     */
    @TableField("exec_user_name")
    private String execUserName;

    /**
     * 执行开始时间
     */
    @TableField("exec_start_time")
    private Date execStartTime;

    /**
     * 执行结束时间
     */
    @TableField("exec_end_time")
    private Date execEndTime;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
