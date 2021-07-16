package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 上线单操作步骤
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("release_action")
public class ReleaseActionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上线单id
     */
    @TableField("release_id")
    private Long releaseId;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

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
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    @TableField("run_status")
    private Integer runStatus;

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
