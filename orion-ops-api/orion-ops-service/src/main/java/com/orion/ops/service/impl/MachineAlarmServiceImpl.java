package com.orion.ops.service.impl;

import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.MachineAlarmHistoryDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineAlarmHistoryDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.machine.MachineAlarmRequest;
import com.orion.ops.handler.alarm.MachineAlarmContext;
import com.orion.ops.handler.alarm.MachineAlarmExecutor;
import com.orion.ops.service.api.MachineAlarmService;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 机器报警服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
@Service("machineAlarmService")
public class MachineAlarmServiceImpl implements MachineAlarmService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineAlarmHistoryDAO machineAlarmHistoryDAO;

    @Override
    public void triggerMachineAlarm(Long alarmHistoryId) {
        // 查询数据
        MachineAlarmHistoryDO history = machineAlarmHistoryDAO.selectById(alarmHistoryId);
        Valid.notNull(history, MessageConst.UNKNOWN_DATA);
        // 查询机器信息
        Long machineId = history.getMachineId();
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 执行通知操作
        MachineAlarmContext context = new MachineAlarmContext();
        context.setMachineId(machineId);
        context.setMachineName(machine.getMachineName());
        context.setMachineHost(machine.getMachineHost());
        context.setAlarmType(history.getAlarmType());
        context.setAlarmValue(history.getAlarmValue());
        context.setAlarmTime(history.getAlarmTime());
        new MachineAlarmExecutor(context).exec();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, alarmHistoryId);
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
    }

    @Override
    public void triggerMachineAlarm(MachineAlarmRequest request) {
        // 查询机器信息
        Long machineId = request.getMachineId();
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 历史通知
        MachineAlarmHistoryDO history = new MachineAlarmHistoryDO();
        history.setMachineId(machineId);
        history.setAlarmType(request.getType());
        history.setAlarmValue(request.getAlarmValue());
        history.setAlarmTime(request.getAlarmTime());
        machineAlarmHistoryDAO.insert(history);
        // 执行通知操作
        MachineAlarmContext context = new MachineAlarmContext();
        context.setMachineId(machineId);
        context.setMachineName(machine.getMachineName());
        context.setMachineHost(machine.getMachineHost());
        context.setAlarmType(request.getType());
        context.setAlarmValue(request.getAlarmValue());
        context.setAlarmTime(request.getAlarmTime());
        new MachineAlarmExecutor(context).exec();
    }

}
