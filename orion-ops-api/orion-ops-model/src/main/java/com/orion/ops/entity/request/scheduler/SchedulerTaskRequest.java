package com.orion.ops.entity.request.scheduler;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "调度任务请求")
@SuppressWarnings("ALL")
public class SchedulerTaskRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "执行命令")
    private String command;

    @ApiModelProperty(value = "cron表达式")
    private String expression;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "启用状态 1启用 2停用")
    private Integer enableStatus;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止")
    private Integer latelyStatus;

    /**
     * @see com.orion.ops.constant.SerialType
     */
    @ApiModelProperty(value = "调度序列 10串行 20并行")
    private Integer serializeType;

    /**
     * @see com.orion.ops.constant.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "次数")
    private Integer times;

}
