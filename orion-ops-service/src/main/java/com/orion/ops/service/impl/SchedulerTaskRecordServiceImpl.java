package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus;
import com.orion.ops.consts.scheduler.SchedulerTaskStatus;
import com.orion.ops.dao.SchedulerTaskDAO;
import com.orion.ops.dao.SchedulerTaskMachineRecordDAO;
import com.orion.ops.dao.SchedulerTaskRecordDAO;
import com.orion.ops.entity.domain.*;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 调度任务执行日志 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Service("schedulerTaskRecordService")
public class SchedulerTaskRecordServiceImpl implements SchedulerTaskRecordService {

    @Resource
    private SchedulerTaskDAO schedulerTaskDAO;

    @Resource
    private SchedulerTaskMachineService schedulerTaskMachineService;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Override
    public Integer deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<SchedulerTaskRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskRecordDO>()
                .eq(SchedulerTaskRecordDO::getTaskId, taskId);
        return schedulerTaskRecordDAO.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTaskRecord(Long taskId) {
        // 查询任务
        SchedulerTaskDO task = schedulerTaskDAO.selectById(taskId);
        Valid.notNull(task);
        List<SchedulerTaskMachineDO> machines = schedulerTaskMachineService.selectByTaskId(taskId);
        boolean emptyMachine = machines.isEmpty();
        Date now = new Date();
        // 创建明细
        SchedulerTaskRecordDO record = new SchedulerTaskRecordDO();
        record.setTaskId(taskId);
        record.setTaskName(task.getTaskName());
        record.setTaskStatus(emptyMachine ? SchedulerTaskStatus.SUCCESS.getStatus() : SchedulerTaskStatus.WAIT.getStatus());
        record.setCreateTime(now);
        schedulerTaskRecordDAO.insert(record);
        Long recordId = record.getId();
        // 创建机器明细
        if (!emptyMachine) {
            String command = task.getTaskCommand();
            final boolean containsEnv = command.contains(EnvConst.SYMBOL);
            if (containsEnv) {
                // 获取系统变量
                Map<String, String> systemEnv = systemEnvService.getFullSystemEnv();
                command = Strings.format(command, EnvConst.SYMBOL, systemEnv);
            }
            for (SchedulerTaskMachineDO taskMachine : machines) {
                Long machineId = taskMachine.getMachineId();

                // 查询机器信息
                MachineInfoDO machine = machineInfoService.selectById(machineId);
                if (machine == null) {
                    continue;
                }
                // 设置机器明细
                SchedulerTaskMachineRecordDO machineRecord = new SchedulerTaskMachineRecordDO();
                machineRecord.setTaskId(taskId);
                machineRecord.setRecordId(recordId);
                machineRecord.setTaskMachineId(taskMachine.getId());
                machineRecord.setMachineId(machineId);
                machineRecord.setMachineName(machine.getMachineName());
                machineRecord.setMachineHost(machine.getMachineHost());
                machineRecord.setMachineTag(machine.getMachineTag());
                if (containsEnv) {
                    // 查询机器变量
                    MutableLinkedHashMap<String, String> machineEnv = machineEnvService.getFullMachineEnv(machineId);
                    machineRecord.setExecCommand(Strings.format(command, EnvConst.SYMBOL, machineEnv));
                } else {
                    machineRecord.setExecCommand(command);
                }
                machineRecord.setExecStatus(SchedulerTaskMachineStatus.WAIT.getStatus());
                machineRecord.setLogPath(PathBuilders.getSchedulerTaskLogPath(taskId, recordId, machineId));
                schedulerTaskMachineRecordDAO.insert(machineRecord);
            }
        }
        // 更新任务状态
        SchedulerTaskDO updateTask = new SchedulerTaskDO();
        updateTask.setId(taskId);
        updateTask.setLatelyStatus(record.getTaskStatus());
        updateTask.setLatelyScheduleTime(now);
        updateTask.setUpdateTime(now);
        schedulerTaskDAO.updateById(updateTask);
        return recordId;
    }

}
