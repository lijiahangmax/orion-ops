package com.orion.ops.service.api;

import com.orion.ops.entity.request.machine.MachineAlarmRequest;

/**
 * 机器报警服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
public interface MachineAlarmService {

    /**
     * 触发机器报警
     *
     * @param alarmHistoryId alarmHistoryId
     */
    void triggerMachineAlarm(Long alarmHistoryId);

    /**
     * 触发机器报警
     *
     * @param request request
     */
    void triggerMachineAlarm(MachineAlarmRequest request);

}
