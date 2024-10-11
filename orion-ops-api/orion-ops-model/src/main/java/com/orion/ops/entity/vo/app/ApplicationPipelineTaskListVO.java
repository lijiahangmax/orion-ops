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
package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流水线明细详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
@ApiModel(value = "流水线明细详情响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskListVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线名称")
    private String pipelineName;

    @ApiModelProperty(value = "执行标题")
    private String title;

    @ApiModelProperty(value = "执行描述")
    private String description;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    @ApiModelProperty(value = "是否是定时执行 10普通执行 20定时执行")
    private Integer timedExec;

    @ApiModelProperty(value = "定时执行时间")
    private Date timedExecTime;

    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "执行人id")
    private Long execUserId;

    @ApiModelProperty(value = "执行人名称")
    private String execUserName;

    @ApiModelProperty(value = "执行开始时间")
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    private Date execEndTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

}
