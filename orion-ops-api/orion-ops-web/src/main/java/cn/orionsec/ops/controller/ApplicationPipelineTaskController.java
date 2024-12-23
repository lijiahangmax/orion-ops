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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.IgnoreLog;
import cn.orionsec.ops.annotation.RequireRole;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.app.PipelineStatus;
import cn.orionsec.ops.constant.app.TimedType;
import cn.orionsec.ops.constant.common.AuditStatus;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineTaskDetailRequest;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineTaskRequest;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskDetailService;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskLogService;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskService;
import cn.orionsec.ops.task.TaskRegister;
import cn.orionsec.ops.task.TaskType;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 应用流水线任务 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:54
 */
@Api(tags = "应用流水线任务")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline-task")
public class ApplicationPipelineTaskController {

    @Resource
    private ApplicationPipelineTaskService applicationPipelineTaskService;

    @Resource
    private ApplicationPipelineTaskDetailService applicationPipelineTaskDetailService;

    @Resource
    private ApplicationPipelineTaskLogService applicationPipelineTaskLogService;

    @Resource
    private TaskRegister taskRegister;

    @PostMapping("/list")
    @ApiOperation(value = "获取任务列表")
    public DataGrid<ApplicationPipelineTaskListVO> getPipelineTaskList(@RequestBody ApplicationPipelineTaskRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineTaskService.getPipelineTaskList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取任务详情")
    public ApplicationPipelineTaskVO getPipelineTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.getPipelineTaskDetail(id);
    }

    @PostMapping("/task-details")
    @ApiOperation(value = "获取任务执行详情")
    public List<ApplicationPipelineTaskDetailVO> getTaskDetails(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskDetailService.getTaskDetails(id);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交任务")
    @EventLog(EventType.SUBMIT_PIPELINE_TASK)
    public Long submitPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Valid.notNull(request.getPipelineId());
        Valid.notBlank(request.getTitle());
        List<ApplicationPipelineTaskDetailRequest> details = Valid.notEmpty(request.getDetails());
        for (ApplicationPipelineTaskDetailRequest detail : details) {
            Valid.notNull(detail.getId());
        }
        TimedType timedType = Valid.notNull(TimedType.of(request.getTimedExec()));
        if (TimedType.TIMED.equals(timedType)) {
            Date timedExecTime = Valid.notNull(request.getTimedExecTime());
            Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        }
        Long id = applicationPipelineTaskService.submitPipelineTask(request);
        // 提交任务
        if (PipelineStatus.WAIT_SCHEDULE.getStatus().equals(request.getStatus())) {
            taskRegister.submit(TaskType.PIPELINE, request.getTimedExecTime(), id);
        }
        return id;
    }

    @PostMapping("/audit")
    @ApiOperation(value = "审核任务")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.AUDIT_PIPELINE_TASK)
    public Integer auditPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Valid.notNull(request.getId());
        AuditStatus status = Valid.notNull(AuditStatus.of(request.getAuditStatus()));
        if (AuditStatus.REJECT.equals(status)) {
            Valid.notBlank(request.getAuditReason());
        }
        return applicationPipelineTaskService.auditPipelineTask(request);
    }

    @PostMapping("/copy")
    @ApiOperation(value = "复制任务")
    @EventLog(EventType.COPY_PIPELINE_TASK)
    public Long copyPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.copyPipeline(id);
    }

    @PostMapping("/exec")
    @ApiOperation(value = "执行任务")
    @EventLog(EventType.EXEC_PIPELINE_TASK)
    public HttpWrapper<?> execPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.execPipeline(id, false);
        return HttpWrapper.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除任务")
    @EventLog(EventType.DELETE_PIPELINE_TASK)
    public Integer deletePipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineTaskService.deletePipeline(idList);
    }

    @PostMapping("/set-timed")
    @ApiOperation(value = "设置定时执行")
    @EventLog(EventType.SET_PIPELINE_TIMED_TASK)
    public HttpWrapper<?> setTaskTimedExec(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Date timedExecTime = Valid.notNull(request.getTimedExecTime());
        Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        applicationPipelineTaskService.setPipelineTimedExec(id, timedExecTime);
        return HttpWrapper.ok();
    }

    @PostMapping("/cancel-timed")
    @ApiOperation(value = "取消定时")
    @EventLog(EventType.CANCEL_PIPELINE_TIMED_TASK)
    public HttpWrapper<?> cancelTaskTimedExec(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.cancelPipelineTimedExec(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/terminate")
    @ApiOperation(value = "停止执行任务")
    @EventLog(EventType.TERMINATE_PIPELINE_TASK)
    public HttpWrapper<?> terminateTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.terminateExec(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/terminate-detail")
    @ApiOperation(value = "停止部分操作")
    @EventLog(EventType.TERMINATE_PIPELINE_TASK_DETAIL)
    public HttpWrapper<?> terminateTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Long detailId = Valid.notNull(request.getDetailId());
        applicationPipelineTaskService.terminateExecDetail(id, detailId);
        return HttpWrapper.ok();
    }

    @PostMapping("/skip-detail")
    @ApiOperation(value = "跳过部分操作")
    @EventLog(EventType.SKIP_PIPELINE_TASK_DETAIL)
    public HttpWrapper<?> skipTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Long detailId = Valid.notNull(request.getDetailId());
        applicationPipelineTaskService.skipExecDetail(id, detailId);
        return HttpWrapper.ok();
    }

    @IgnoreLog
    @PostMapping("/status")
    @ApiOperation(value = "获取单个任务状态")
    public ApplicationPipelineTaskStatusVO getTaskStatus(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.getTaskStatus(id);
    }

    @IgnoreLog
    @PostMapping("/list-status")
    @ApiOperation(value = "获取多个任务状态")
    public List<ApplicationPipelineTaskStatusVO> getTaskStatusList(@RequestBody ApplicationPipelineTaskRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineTaskService.getTaskStatusList(idList, request.getDetailIdList());
    }

    @PostMapping("/log")
    @ApiOperation(value = "获取任务日志")
    public List<ApplicationPipelineTaskLogVO> getTaskLogList(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskLogService.getLogList(id);
    }

}
