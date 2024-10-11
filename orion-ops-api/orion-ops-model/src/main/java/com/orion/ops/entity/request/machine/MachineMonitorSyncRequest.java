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

import com.orion.ops.entity.vo.machine.MachineAlarmConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器监控同步请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/1 14:49
 */
@Data
@ApiModel(value = "机器监控同步请求")
public class MachineMonitorSyncRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "报警配置")
    private List<MachineAlarmConfigVO> alarmConfig;

}
