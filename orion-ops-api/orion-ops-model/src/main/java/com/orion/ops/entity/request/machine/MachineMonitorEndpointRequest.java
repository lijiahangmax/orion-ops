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
package com.orion.ops.entity.request.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器端点请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/6 14:48
 */
@Data
@ApiModel(value = "机器监控请求")
public class MachineMonitorEndpointRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "数据粒度")
    private Integer granularity;

    @ApiModelProperty(value = "开始区间 (秒)")
    private Long startRange;

    @ApiModelProperty(value = "结束区间 (秒)")
    private Long endRange;

    @ApiModelProperty(value = "磁盘序列")
    private String seq;
}
