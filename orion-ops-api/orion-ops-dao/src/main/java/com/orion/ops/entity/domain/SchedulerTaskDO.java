/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度任务
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "调度任务")
@TableName("scheduler_task")
@SuppressWarnings("ALL")
public class SchedulerTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务名称")
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty(value = "任务描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "执行命令")
    @TableField("task_command")
    private String taskCommand;

    @ApiModelProperty(value = "cron表达式")
    @TableField("expression")
    private String expression;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "启用状态 1启用 2停用")
    @TableField("enable_status")
    private Integer enableStatus;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止")
    @TableField("lately_status")
    private Integer latelyStatus;

    /**
     * @see com.orion.ops.constant.common.SerialType
     */
    @ApiModelProperty(value = "调度序列 10串行 20并行")
    @TableField("serialize_type")
    private Integer serializeType;

    /**
     * @see com.orion.ops.constant.common.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    @TableField("exception_handler")
    private Integer exceptionHandler;

    @ApiModelProperty(value = "上次调度时间")
    @TableField(value = "lately_schedule_time")
    private Date latelyScheduleTime;

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
