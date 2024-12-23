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
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器报警历史
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_alarm_history")
@ApiModel(value = "MachineAlarmHistoryDO对象", description = "机器报警历史")
@SuppressWarnings("ALL")
public class MachineAlarmHistoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    /**
     * @see cn.orionsec.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    @TableField("alarm_type")
    private Integer alarmType;

    @ApiModelProperty(value = "报警值")
    @TableField("alarm_value")
    private Double alarmValue;

    @ApiModelProperty(value = "报警时间")
    @TableField("alarm_time")
    private Date alarmTime;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
