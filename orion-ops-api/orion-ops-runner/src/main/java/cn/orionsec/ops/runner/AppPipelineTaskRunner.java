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
package cn.orionsec.ops.runner;

import cn.orionsec.ops.constant.app.PipelineDetailStatus;
import cn.orionsec.ops.constant.app.PipelineStatus;
import cn.orionsec.ops.dao.ApplicationPipelineTaskDAO;
import cn.orionsec.ops.dao.ApplicationPipelineTaskDetailDAO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskDetailService;
import cn.orionsec.ops.task.TaskRegister;
import cn.orionsec.ops.task.TaskType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
