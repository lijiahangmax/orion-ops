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
 * 构建统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:03
 */
@Data
@ApiModel(value = "构建统计响应")
public class ApplicationBuildStatisticsMetricsVO {

    @ApiModelProperty(value = "构建次数")
    private Integer buildCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功平均构建时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均构建时长")
    private String avgUsedInterval;

}
