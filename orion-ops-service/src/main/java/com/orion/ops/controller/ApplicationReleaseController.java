package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.ApplicationReleaseService;
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
 * 应用发布api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 22:15
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-release")
public class ApplicationReleaseController {

    @Resource
    private ApplicationReleaseService applicationReleaseService;

    @Resource
    private TaskRegister taskRegister;

    /**
     * 发布列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationReleaseListVO> getReleaseList(@RequestBody ApplicationReleaseRequest request) {
        return applicationReleaseService.getReleaseList(request);
    }

    /**
     * 发布机器列表
     */
    @RequestMapping("/list-machine")
    public List<ApplicationReleaseMachineVO> getReleaseMachineList(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseMachineList(id);
    }

    /**
     * 发布详情
     */
    @RequestMapping("/detail")
    public ApplicationReleaseDetailVO getReleaseDetail(@RequestBody ApplicationReleaseRequest request) {
        Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseDetail(request);
    }

    /**
     * 发布机器详情
     */
    @RequestMapping("/machine-detail")
    public ApplicationReleaseMachineVO getReleaseMachineDetail(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        return applicationReleaseService.getReleaseMachineDetail(releaseMachineId);
    }

    /**
     * 提交发布
     */
    @RequestMapping("/submit")
    @EventLog(EventType.SUBMIT_RELEASE)
    public Long submitAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Valid.notBlank(request.getTitle());
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        Valid.notNull(request.getBuildId());
        Valid.notEmpty(request.getMachineIdList());
        TimedType timedType = Valid.notNull(TimedType.of(request.getTimedRelease()));
        if (TimedType.TIMED.equals(timedType)) {
            Date timedReleaseTime = Valid.notNull(request.getTimedReleaseTime());
            Valid.isTrue(timedReleaseTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        }
        // 提交
        Long id = applicationReleaseService.submitAppRelease(request);
        // 提交任务
        if (ReleaseStatus.WAIT_SCHEDULE.getStatus().equals(request.getStatus())) {
            taskRegister.submit(TaskType.RELEASE, request.getTimedReleaseTime(), id);
        }
        return id;
    }

    /**
     * 复制发布
     */
    @RequestMapping("/copy")
    @EventLog(EventType.COPY_RELEASE)
    public Long copyAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.copyAppRelease(id);
    }

    /**
     * 发布审核
     */
    @RequestMapping("/audit")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.AUDIT_RELEASE)
    public Integer auditAppRelease(@RequestBody ApplicationReleaseAuditRequest request) {
        Valid.notNull(request.getId());
        AuditStatus status = Valid.notNull(AuditStatus.of(request.getStatus()));
        if (AuditStatus.REJECT.equals(status)) {
            Valid.notBlank(request.getReason());
        }
        return applicationReleaseService.auditAppRelease(request);
    }

    /**
     * 执行发布
     */
    @RequestMapping("/runnable")
    @EventLog(EventType.RUNNABLE_RELEASE)
    public HttpWrapper<?> runnableAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.runnableAppRelease(id, false, true);
        return HttpWrapper.ok();
    }

    /**
     * 取消定时发布
     */
    @RequestMapping("/cancel-timed")
    @EventLog(EventType.CANCEL_TIMED_RELEASE)
    public HttpWrapper<?> cancelTimedRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.cancelAppTimedRelease(id);
        return HttpWrapper.ok();
    }

    /**
     * 设置定时发布
     */
    @RequestMapping("/set-timed")
    @EventLog(EventType.SET_TIMED_RELEASE)
    public HttpWrapper<?> setTimedRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        Date timedReleaseTime = Valid.notNull(request.getTimedReleaseTime());
        Valid.isTrue(timedReleaseTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        applicationReleaseService.setTimedRelease(id, timedReleaseTime);
        return HttpWrapper.ok();
    }

    /**
     * 回滚发布
     */
    @RequestMapping("/rollback")
    @EventLog(EventType.ROLLBACK_RELEASE)
    public Long rollbackAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.rollbackAppRelease(id);
    }

    /**
     * 发布停止
     */
    @RequestMapping("/terminate")
    @EventLog(EventType.TERMINATE_RELEASE)
    public HttpWrapper<?> terminateAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.terminateRelease(id);
        return HttpWrapper.ok();
    }

    /**
     * 停止机器
     */
    @RequestMapping("/terminate-machine")
    @EventLog(EventType.TERMINATE_MACHINE_RELEASE)
    public HttpWrapper<?> terminateMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        applicationReleaseService.terminateMachine(releaseMachineId);
        return HttpWrapper.ok();
    }

    /**
     * 跳过机器
     */
    @RequestMapping("/skip-machine")
    @EventLog(EventType.SKIP_MACHINE_RELEASE)
    public HttpWrapper<?> skipMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        applicationReleaseService.skipMachine(releaseMachineId);
        return HttpWrapper.ok();
    }

    /**
     * 输入命令
     */
    @RequestMapping("/write-machine")
    public HttpWrapper<?> writeMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        String command = Valid.notEmpty(request.getCommand());
        applicationReleaseService.writeMachine(releaseMachineId, command);
        return HttpWrapper.ok();
    }

    /**
     * 发布删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_RELEASE)
    public Integer deleteAppRelease(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return applicationReleaseService.deleteRelease(idList);
    }

    /**
     * 发布状态列表
     */
    @IgnoreLog
    @RequestMapping("/list-status")
    public List<ApplicationReleaseStatusVO> getReleaseStatusList(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationReleaseService.getReleaseStatusList(idList, request.getMachineIdList());
    }

    /**
     * 发布状态
     */
    @IgnoreLog
    @RequestMapping("/status")
    public ApplicationReleaseStatusVO getReleaseStatus(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseStatus(id);
    }

    /**
     * 发布机器状态列表
     */
    @IgnoreLog
    @RequestMapping("/list-machine-status")
    public List<ApplicationReleaseMachineStatusVO> getReleaseMachineStatusList(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notEmpty(request.getReleaseMachineIdList());
        return applicationReleaseService.getReleaseMachineStatusList(idList);
    }

    /**
     * 发布机器状态
     */
    @IgnoreLog
    @RequestMapping("/machine-status")
    public ApplicationReleaseMachineStatusVO getReleaseMachineStatus(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getReleaseMachineId());
        return applicationReleaseService.getReleaseMachineStatus(id);
    }

}
