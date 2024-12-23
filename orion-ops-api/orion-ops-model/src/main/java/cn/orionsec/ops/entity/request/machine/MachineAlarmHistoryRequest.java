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
package cn.orionsec.ops.entity.request.machine;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 机器报警历史请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/31 10:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器报警历史请求")
@SuppressWarnings("ALL")
public class MachineAlarmHistoryRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "machineId")
    private Long machineId;

    /**
     * @see cn.orionsec.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    private Integer type;

    @ApiModelProperty(value = "报警值区间开始")
    private Double alarmValueStart;

    @ApiModelProperty(value = "报警值区间结束")
    private Double alarmValueEnd;

    @ApiModelProperty(value = "报警时间区间开始")
    private Date alarmTimeStart;

    @ApiModelProperty(value = "报警时间区间结束")
    private Date alarmTimeEnd;

}
