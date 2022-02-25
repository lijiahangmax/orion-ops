package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 调度任务请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 11:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulerTaskRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行命令
     */
    private String command;

    /**
     * cron表达式
     */
    private String expression;

    /**
     * 启用状态 1启用 2停用
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer enableStatus;

    /**
     * 最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskStatus
     */
    private Integer latelyStatus;

    /**
     * 调度序列 10串行 20并行
     *
     * @see com.orion.ops.consts.SerialType
     */
    private Integer serializeType;

    /**
     * 异常处理 10跳过 20继续
     *
     * @see com.orion.ops.consts.ExceptionHandlerType
     */
    private Integer exceptionHandler;

    /**
     * 机器id
     */
    private List<Long> machineIdList;

    /**
     * 次数
     */
    private Integer times;

}
