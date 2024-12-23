/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
 * 应用流水线详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:40
 */
@Data
@ApiModel(value = "应用流水线详情响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineStatisticsDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    @ApiModelProperty(value = "成功平均操作时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成平均操作时长")
    private String avgUsedInterval;

}
