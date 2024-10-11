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
 * 流水线日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:49
 */
@Data
@ApiModel(value = "流水线日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "流水线任务详情id")
    private Long taskDetailId;

    /**
     * @see com.orion.ops.constant.app.PipelineLogStatus
     */
    @ApiModelProperty(value = "日志状态 10创建 20执行 30成功 40失败 50停止 60跳过")
    private Integer status;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer type;

    @ApiModelProperty(value = "日志详情")
    private String log;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

}
