/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.app.ReleaseStatus;
import com.orion.ops.constant.app.TimedType;
import com.orion.ops.constant.common.AuditStatus;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.request.app.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.app.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.service.api.ApplicationReleaseService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.utils.Valid;
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
 * 应用发布 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 22:15
 */
@Api(tags = "应用发布")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-release")
public class ApplicationReleaseController {

    @Resource
    private ApplicationReleaseService applicationReleaseService;

    @Resource
    private TaskRegister taskRegister;

    @PostMapping("/list")
    @ApiOperation(value = "获取发布列表")
    public DataGrid<ApplicationReleaseListVO> getReleaseList(@RequestBody ApplicationReleaseRequest request) {
        return applicationReleaseService.getReleaseList(request);
    }

    @PostMapping("/list-machine")
    @ApiOperation(value = "获取发布机器列表")
    public List<ApplicationReleaseMachineVO> getReleaseMachineList(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseMachineList(id);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取发布详情")
    public ApplicationReleaseDetailVO getReleaseDetail(@RequestBody ApplicationReleaseRequest request) {
        Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseDetail(request);
    }

    @PostMapping("/machine-detail")
    @ApiOperation(value = "获取发布机器详情")
    public ApplicationReleaseMachineVO getReleaseMachineDetail(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        return applicationReleaseService.getReleaseMachineDetail(releaseMachineId);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交发布任务")
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

    @PostMapping("/copy")
    @ApiOperation(value = "复制发布任务")
    @EventLog(EventType.COPY_RELEASE)
    public Long copyAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.copyAppRelease(id);
    }

    @PostMapping("/audit")
    @ApiOperation(value = "发布任务审核")
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

    @PostMapping("/runnable")
    @ApiOperation(value = "执行发布任务")
    @EventLog(EventType.RUNNABLE_RELEASE)
    public HttpWrapper<?> runnableAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.runnableAppRelease(id, false, true);
        return HttpWrapper.ok();
    }

    @PostMapping("/set-timed")
    @ApiOperation(value = "设置定时发布")
    @EventLog(EventType.SET_TIMED_RELEASE)
    public HttpWrapper<?> setTimedRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        Date timedReleaseTime = Valid.notNull(request.getTimedReleaseTime());
        Valid.isTrue(timedReleaseTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        applicationReleaseService.setTimedRelease(id, timedReleaseTime);
        return HttpWrapper.ok();
    }

    @PostMapping("/cancel-timed")
    @ApiOperation(value = "取消定时发布")
    @EventLog(EventType.CANCEL_TIMED_RELEASE)
    public HttpWrapper<?> cancelTimedRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.cancelAppTimedRelease(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/rollback")
    @ApiOperation(value = "回滚发布")
    @EventLog(EventType.ROLLBACK_RELEASE)
    public Long rollbackAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.rollbackAppRelease(id);
    }

    @PostMapping("/terminate")
    @EventLog(EventType.TERMINATE_RELEASE)
    @ApiOperation(value = "停止发布任务")
    public HttpWrapper<?> terminateAppRelease(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationReleaseService.terminateRelease(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/terminate-machine")
    @ApiOperation(value = "停止机器发布操作")
    @EventLog(EventType.TERMINATE_MACHINE_RELEASE)
    public HttpWrapper<?> terminateMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        applicationReleaseService.terminateMachine(releaseMachineId);
        return HttpWrapper.ok();
    }

    @PostMapping("/skip-machine")
    @ApiOperation(value = "跳过机器发布操作")
    @EventLog(EventType.SKIP_MACHINE_RELEASE)
    public HttpWrapper<?> skipMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        applicationReleaseService.skipMachine(releaseMachineId);
        return HttpWrapper.ok();
    }

    @PostMapping("/write-machine")
    @ApiOperation(value = "机器操作输入命令")
    public HttpWrapper<?> writeMachine(@RequestBody ApplicationReleaseRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        String command = Valid.notEmpty(request.getCommand());
        applicationReleaseService.writeMachine(releaseMachineId, command);
        return HttpWrapper.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除发布任务")
    @EventLog(EventType.DELETE_RELEASE)
    public Integer deleteAppRelease(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return applicationReleaseService.deleteRelease(idList);
    }

    @IgnoreLog
    @PostMapping("/list-status")
    @ApiOperation(value = "获取发布状态列表")
    public List<ApplicationReleaseStatusVO> getReleaseStatusList(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationReleaseService.getReleaseStatusList(idList, request.getMachineIdList());
    }

    @IgnoreLog
    @PostMapping("/status")
    @ApiOperation(value = "获取发布状态")
    public ApplicationReleaseStatusVO getReleaseStatus(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationReleaseService.getReleaseStatus(id);
    }

    @IgnoreLog
    @PostMapping("/list-machine-status")
    @ApiOperation(value = "获取发布机器状态列表")
    public List<ApplicationReleaseMachineStatusVO> getReleaseMachineStatusList(@RequestBody ApplicationReleaseRequest request) {
        List<Long> idList = Valid.notEmpty(request.getReleaseMachineIdList());
        return applicationReleaseService.getReleaseMachineStatusList(idList);
    }

    @IgnoreLog
    @PostMapping("/machine-status")
    @ApiOperation(value = "获取发布机器状态")
    public ApplicationReleaseMachineStatusVO getReleaseMachineStatus(@RequestBody ApplicationReleaseRequest request) {
        Long id = Valid.notNull(request.getReleaseMachineId());
        return applicationReleaseService.getReleaseMachineStatus(id);
    }

}
