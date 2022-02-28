package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 调度任务
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scheduler_task")
public class SchedulerTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 执行命令
     */
    @TableField("task_command")
    private String taskCommand;

    /**
     * cron表达式
     */
    @TableField("expression")
    private String expression;

    /**
     * 启用状态 1启用 2停用
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @TableField("enable_status")
    private Integer enableStatus;

    /**
     * 最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskStatus
     */
    @TableField("lately_status")
    private Integer latelyStatus;

    /**
     * 调度序列 10串行 20并行
     *
     * @see com.orion.ops.consts.SerialType
     */
    @TableField("serialize_type")
    private Integer serializeType;

    /**
     * 异常处理 10跳过所有 20跳过错误
     *
     * @see com.orion.ops.consts.ExceptionHandlerType
     */
    @TableField("exception_handler")
    private Integer exceptionHandler;

    /**
     * 上次调度时间
     */
    @TableField(value = "lately_schedule_time")
    private Date latelyScheduleTime;

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
