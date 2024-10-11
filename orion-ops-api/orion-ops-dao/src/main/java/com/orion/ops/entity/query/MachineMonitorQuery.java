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
package com.orion.ops.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器监控查询参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/2 10:22
 */
@Data
@ApiModel(value = "机器监控查询参数")
@SuppressWarnings("ALL")
public class MachineMonitorQuery {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see com.orion.ops.constant.monitor.MonitorStatus
     */
    @ApiModelProperty(value = "监控状态 1未安装 2安装中 3未运行 4运行中")
    private Integer monitorStatus;

}
