package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 调度任务执行明细机器详情
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scheduler_task_machine_record")
public class SchedulerTaskMachineRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 明细id
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 任务机器id
     */
    @TableField("task_machine_id")
    private Long taskMachineId;

    /**
     * 执行机器id
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
     * 机器唯一标识
     */
    @TableField("machine_tag")
    private String machineTag;

    /**
     * 执行命令
     */
    @TableField("exec_command")
    private String execCommand;

    /**
     * 执行状态 10待调度 20调度中 30调度成功 40调度失败 50已跳过 60已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus
     */
    @TableField("exec_status")
    private Integer execStatus;

    /**
     * 退出码
     */
    @TableField("exit_code")
    private Integer exitCode;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 日志路径
     */
    @TableField("log_path")
    private String logPath;

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
