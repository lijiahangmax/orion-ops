package com.orion.ops.handler.alarm;

import com.orion.ops.entity.domain.UserInfoDO;
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
     * @see com.orion.ops.constant.machine.MachineAlarmType
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
