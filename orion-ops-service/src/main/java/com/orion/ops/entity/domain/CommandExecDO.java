package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 命令执行表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("command_exec")
public class CommandExecDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 执行类型 10批量执行
     *
     * @see com.orion.ops.consts.command.ExecType
     */
    @TableField("exec_type")
    private Integer execType;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * 机器名称
     */
    @TableField("machine_name")
    private String machineName;

    /**
     * 机器主机
     */
    @TableField("machine_host")
    private String machineHost;

    /**
     * 机器tag
     */
    @TableField("machine_tag")
    private String machineTag;

    /**
     * 执行状态 10未开始 20执行中 30执行成功 40执行失败 50执行终止
     *
     * @see com.orion.ops.consts.command.ExecStatus
     */
    @TableField("exec_status")
    private Integer execStatus;

    /**
     * 执行返回码
     */
    @TableField("exit_code")
    private Integer exitCode;

    /**
     * 命令
     */
    @TableField("exec_command")
    private String execCommand;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 日志目录
     */
    @TableField("log_path")
    private String logPath;

    /**
     * 执行开始时间
     */
    @TableField("start_date")
    private Date startDate;

    /**
     * 执行结束时间
     */
    @TableField("end_date")
    private Date endDate;

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
