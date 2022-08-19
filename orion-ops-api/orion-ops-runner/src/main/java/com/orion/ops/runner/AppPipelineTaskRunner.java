package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.app.PipelineDetailStatus;
import com.orion.ops.constant.app.PipelineStatus;
import com.orion.ops.dao.ApplicationPipelineTaskDAO;
import com.orion.ops.dao.ApplicationPipelineTaskDetailDAO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.service.api.ApplicationPipelineTaskDetailService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流水线任务初始化
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 15:13
 */
@Component
@Order(3400)
@Slf4j
public class AppPipelineTaskRunner implements CommandLineRunner {

    @Resource
    private ApplicationPipelineTaskDAO applicationPipelineTaskDAO;

    @Resource
    private ApplicationPipelineTaskDetailDAO applicationPipelineTaskDetailDAO;

    @Resource
    private ApplicationPipelineTaskDetailService applicationPipelineTaskDetailService;

    @Resource
    private TaskRegister taskRegister;

    @Override
    public void run(String... args) {
        log.info("流水线任务初始化-开始");
        // 更新执行任务状态
        this.updateTaskStatus();
        // 自动恢复任务
        this.autoResumeTask();
        log.info("流水线任务初始化-结束");
    }

    /**
     * 更新任务执行状态
     */
    private void updateTaskStatus() {
        // 查询任务
        Wrapper<ApplicationPipelineTaskDO> taskWrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDO>()
                .in(ApplicationPipelineTaskDO::getExecStatus, PipelineStatus.RUNNABLE.getStatus());
        List<ApplicationPipelineTaskDO> taskList = applicationPipelineTaskDAO.selectList(taskWrapper);
        if (taskList.isEmpty()) {
            return;
        }
        List<Long> taskIdList = taskList.stream()
                .map(ApplicationPipelineTaskDO::getId)
                .collect(Collectors.toList());
        // 更新任务状态
        ApplicationPipelineTaskDO updateTask = new ApplicationPipelineTaskDO();
        updateTask.setExecStatus(PipelineStatus.TERMINATED.getStatus());
        updateTask.setUpdateTime(new Date());
        applicationPipelineTaskDAO.update(updateTask, taskWrapper);
        log.info("流水线任务初始化-重置任务状态 {}", taskIdList);
        // 查询任务明细
        List<ApplicationPipelineTaskDetailDO> details = applicationPipelineTaskDetailService.selectTaskDetails(taskIdList);
        // 更新明细状态
        for (ApplicationPipelineTaskDetailDO detail : details) {
            ApplicationPipelineTaskDetailDO updateDetail = new ApplicationPipelineTaskDetailDO();
            updateDetail.setId(detail.getId());
            updateDetail.setUpdateTime(new Date());
            switch (PipelineDetailStatus.of(detail.getExecStatus())) {
                case WAIT:
                case RUNNABLE:
                    updateDetail.setExecStatus(PipelineDetailStatus.TERMINATED.getStatus());
                    break;
                default:
                    break;
            }
            applicationPipelineTaskDetailDAO.updateById(updateDetail);
        }
    }

    /**
     * 自动恢复任务
     */
    private void autoResumeTask() {
        Wrapper<ApplicationPipelineTaskDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskDO>()
                .eq(ApplicationPipelineTaskDO::getExecStatus, PipelineStatus.WAIT_SCHEDULE.getStatus());
        List<ApplicationPipelineTaskDO> taskList = applicationPipelineTaskDAO.selectList(wrapper);
        for (ApplicationPipelineTaskDO task : taskList) {
            Long id = task.getId();
            log.info("重新加载流水线任务-提交 id: {}", id);
            taskRegister.submit(TaskType.PIPELINE, task.getTimedExecTime(), id);
        }
    }

}
