package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发布单操作步骤
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_release_action")
public class ApplicationReleaseActionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布机器id
     */
    @TableField("release_machine_id")
    private Long releaseMachineId;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * 上线单id
     */
    @TableField("release_id")
    private Long releaseId;

    /**
     * 操作id
     */
    @TableField("action_id")
    private Long actionId;

    /**
     * 操作名称
     */
    @TableField("action_name")
    private String actionName;

    /**
     * 操作类型
     *
     * @see com.orion.ops.consts.app.ActionType
     */
    @TableField("action_type")
    private Integer actionType;

    /**
     * 操作命令
     */
    @TableField("action_command")
    private String actionCommand;

    /**
     * 操作日志路径
     */
    @TableField("log_path")
    private String logPath;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    @TableField("run_status")
    private Integer runStatus;

    /**
     * 退出码
     */
    @TableField("exit_code")
    private Integer exitCode;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

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
