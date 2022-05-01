package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.PipelineStatus;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ApplicationPipelineTaskDetailRequest;
import com.orion.ops.entity.request.ApplicationPipelineTaskRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.ApplicationPipelineTaskDetailService;
import com.orion.ops.service.api.ApplicationPipelineTaskLogService;
import com.orion.ops.service.api.ApplicationPipelineTaskService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.utils.Valid;
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

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationPipelineTaskListVO> getPipelineTaskList(@RequestBody ApplicationPipelineTaskRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineTaskService.getPipelineTaskList(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public ApplicationPipelineTaskVO getPipelineTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.getPipelineTaskDetail(id);
    }

    /**
     * 明细详情
     */
    @RequestMapping("/task-details")
    public List<ApplicationPipelineTaskDetailVO> getTaskDetails(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskDetailService.getTaskDetails(id);
    }

    /**
     * 提交
     */
    @RequestMapping("/submit")
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

    /**
     * 审核
     */
    @RequestMapping("/audit")
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

    /**
     * 复制
     */
    @RequestMapping("/copy")
    @EventLog(EventType.COPY_PIPELINE_TASK)
    public Long copyPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.copyPipeline(id);
    }

    /**
     * 执行
     */
    @RequestMapping("/exec")
    @EventLog(EventType.EXEC_PIPELINE_TASK)
    public HttpWrapper<?> execPipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.execPipeline(id, false);
        return HttpWrapper.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_PIPELINE_TASK)
    public Integer deletePipelineTask(@RequestBody ApplicationPipelineTaskRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineTaskService.deletePipeline(idList);
    }

    /**
     * 设置定时
     */
    @RequestMapping("/set-timed")
    @EventLog(EventType.SET_PIPELINE_TIMED_TASK)
    public HttpWrapper<?> setTaskTimedExec(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Date timedExecTime = Valid.notNull(request.getTimedExecTime());
        Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        applicationPipelineTaskService.setPipelineTimedExec(id, timedExecTime);
        return HttpWrapper.ok();
    }

    /**
     * 取消定时
     */
    @RequestMapping("/cancel-timed")
    @EventLog(EventType.CANCEL_PIPELINE_TIMED_TASK)
    public HttpWrapper<?> cancelTaskTimedExec(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.cancelPipelineTimedExec(id);
        return HttpWrapper.ok();
    }

    /**
     * 停止
     */
    @RequestMapping("/terminate")
    @EventLog(EventType.TERMINATE_PIPELINE_TASK)
    public HttpWrapper<?> terminateTask(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineTaskService.terminateExec(id);
        return HttpWrapper.ok();
    }

    /**
     * 停止部分操作
     */
    @RequestMapping("/terminate-detail")
    @EventLog(EventType.TERMINATE_PIPELINE_TASK_DETAIL)
    public HttpWrapper<?> terminateTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Long detailId = Valid.notNull(request.getDetailId());
        applicationPipelineTaskService.terminateExecDetail(id, detailId);
        return HttpWrapper.ok();
    }

    /**
     * 跳过部分操作
     */
    @RequestMapping("/skip-detail")
    @EventLog(EventType.SKIP_PIPELINE_TASK_DETAIL)
    public HttpWrapper<?> skipTaskDetail(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Long detailId = Valid.notNull(request.getDetailId());
        applicationPipelineTaskService.skipExecDetail(id, detailId);
        return HttpWrapper.ok();
    }

    /**
     * 任务状态
     */
    @IgnoreLog
    @RequestMapping("/status")
    public ApplicationPipelineTaskStatusVO getTaskStatus(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskService.getTaskStatus(id);
    }

    /**
     * 任务状态
     */
    @IgnoreLog
    @RequestMapping("/list-status")
    public List<ApplicationPipelineTaskStatusVO> getTaskStatusList(@RequestBody ApplicationPipelineTaskRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineTaskService.getTaskStatusList(idList, request.getDetailIdList());
    }

    /**
     * 任务日志
     */
    @RequestMapping("/log")
    public List<ApplicationPipelineTaskLogVO> getTaskLogList(@RequestBody ApplicationPipelineTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineTaskLogService.getLogList(id);
    }

}
