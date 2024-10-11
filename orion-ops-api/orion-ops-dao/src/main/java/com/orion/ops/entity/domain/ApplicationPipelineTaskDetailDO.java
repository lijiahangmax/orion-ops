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
 * 应用流水线任务详情
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用流水线任务详情")
@TableName("application_pipeline_task_detail")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水线id")
    @TableField("pipeline_id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线详情id")
    @TableField("pipeline_detail_id")
    private Long pipelineDetailId;

    @ApiModelProperty(value = "流水线操作任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "引用id")
    @TableField("rel_id")
    private Long relId;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    @TableField("app_name")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    @TableField("app_tag")
    private String appTag;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    @TableField("stage_type")
    private Integer stageType;

    /**
     * @see com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO
     */
    @ApiModelProperty(value = "阶段操作配置")
    @TableField("stage_config")
    private String stageConfig;

    /**
     * @see com.orion.ops.constant.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    @TableField("exec_status")
    private Integer execStatus;

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
