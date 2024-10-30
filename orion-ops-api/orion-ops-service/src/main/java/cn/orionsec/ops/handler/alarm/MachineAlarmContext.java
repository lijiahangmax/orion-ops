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
package cn.orionsec.ops.handler.alarm;

import cn.orionsec.ops.constant.machine.MachineAlarmType;
import cn.orionsec.ops.entity.domain.UserInfoDO;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 机器报警参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 18:43
 */
@Data
public class MachineAlarmContext {

    /**
     * 报警机器id
     */
    private Long machineId;

    /**
     * 报警机器名称
     */
    private String machineName;

    /**
     * 报警主机
     */
    private String machineHost;

    /**
     * 报警类型
     *
     * @see MachineAlarmType
     */
    private Integer alarmType;

    /**
     * 报警值
     */
    private Double alarmValue;

    /**
     * 报警时间
     */
    private Date alarmTime;

    /**
     * 用户映射
     */
    private Map<Long, UserInfoDO> userMapping;

}
