package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus;
import com.orion.ops.consts.scheduler.SchedulerTaskStatus;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.SchedulerTaskDAO;
import com.orion.ops.dao.SchedulerTaskMachineRecordDAO;
import com.orion.ops.dao.SchedulerTaskRecordDAO;
import com.orion.ops.entity.domain.SchedulerTaskDO;
import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.entity.domain.SchedulerTaskRecordDO;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 调度任务初始化
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 17:58
 */
@Component
@Order(3300)
@Slf4j
public class SchedulerTaskRunner implements CommandLineRunner {

    @Resource
    private SchedulerTaskDAO schedulerTaskDAO;

    @Resource
    private SchedulerTaskRecordDAO schedulerTaskRecordDAO;

    @Resource
    private SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO;

    @Resource
    private TaskRegister taskRegister;

    @Override
    public void run(String... args) {
        log.info("调度任务初始化-开始");
        // 更新开始状态
        this.updateTaskStatus();
        // 更新执行记录状态
        this.updateTaskRecordStatus();
        // 自动恢复
        this.autoResumeTask();
        log.info("调度任务初始化-结束");
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus() {
        Boolean autoResume = EnableType.of(SystemEnvAttr.RESUME_ENABLE_SCHEDULER_TASK.getValue()).getValue();
        List<SchedulerTaskDO> tasks = schedulerTaskDAO.selectList(null);
        for (SchedulerTaskDO task : tasks) {
            SchedulerTaskDO update = new SchedulerTaskDO();
            update.setId(task.getId());
            if (!autoResume || !Const.ENABLE.equals(task.getEnableStatus())) {
                update.setEnableStatus(Const.DISABLE);
            }
            update.setUpdateTime(new Date());
            // 最近状态
            SchedulerTaskStatus status = SchedulerTaskStatus.of(task.getLatelyStatus());
            switch (status) {
                case WAIT:
                case RUNNABLE:
                    update.setLatelyStatus(SchedulerTaskStatus.TERMINATED.getStatus());
                    break;
                default:
                    break;
            }
            // 更新
            schedulerTaskDAO.updateById(update);
        }
    }

    /**
     * 更新任务执行状态
     */
    private void updateTaskRecordStatus() {
        // 重置任务明细
        Wrapper<SchedulerTaskRecordDO> recordWrapper = new LambdaQueryWrapper<SchedulerTaskRecordDO>()
                .in(SchedulerTaskRecordDO::getTaskStatus, SchedulerTaskStatus.WAIT.getStatus(), SchedulerTaskStatus.RUNNABLE.getStatus());
        SchedulerTaskRecordDO updateRecord = new SchedulerTaskRecordDO();
        updateRecord.setTaskStatus(SchedulerTaskStatus.TERMINATED.getStatus());
        updateRecord.setUpdateTime(new Date());
        schedulerTaskRecordDAO.update(updateRecord, recordWrapper);

        // 重置机器明细
        Wrapper<SchedulerTaskMachineRecordDO> machineWrapper = new LambdaQueryWrapper<SchedulerTaskMachineRecordDO>()
                .in(SchedulerTaskMachineRecordDO::getExecStatus, SchedulerTaskMachineStatus.WAIT.getStatus(), SchedulerTaskMachineStatus.RUNNABLE.getStatus());
        SchedulerTaskMachineRecordDO updateMachine = new SchedulerTaskMachineRecordDO();
        updateMachine.setExecStatus(SchedulerTaskMachineStatus.TERMINATED.getStatus());
        updateMachine.setUpdateTime(new Date());
        schedulerTaskMachineRecordDAO.update(updateMachine, machineWrapper);

    }

    /**
     * 自动恢复任务
     */
    private void autoResumeTask() {
        Boolean autoResume = EnableType.of(SystemEnvAttr.RESUME_ENABLE_SCHEDULER_TASK.getValue()).getValue();
        if (!autoResume) {
            return;
        }
        // 查询启用的定时任务
        LambdaQueryWrapper<SchedulerTaskDO> wrapper = new LambdaQueryWrapper<SchedulerTaskDO>()
                .eq(SchedulerTaskDO::getEnableStatus, Const.ENABLE);
        List<SchedulerTaskDO> taskList = schedulerTaskDAO.selectList(wrapper);
        // 启用
        for (SchedulerTaskDO task : taskList) {
            Long id = task.getId();
            log.info("调度任务自动恢复 id: {}, name: {}", id, task.getTaskName());
            taskRegister.submit(TaskType.SCHEDULER_TASK, task.getExpression(), id);
        }
    }

}
