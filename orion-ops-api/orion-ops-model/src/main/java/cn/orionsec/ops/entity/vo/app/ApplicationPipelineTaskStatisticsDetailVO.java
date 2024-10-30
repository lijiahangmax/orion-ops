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
package cn.orionsec.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:05
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskStatisticsDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作id")
    private Long detailId;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    /**
     * @see cn.orionsec.ops.constant.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    private Integer status;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private Long used;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private String usedInterval;

}
