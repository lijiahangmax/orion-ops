package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.PipelineStatus;
import com.orion.ops.consts.app.TimedType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ApplicationPipelineDetailRecordRequest;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineDetailRecordVO;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.ApplicationPipelineRecordService;
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
 * 应用流水线明细 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:54
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline-record")
public class ApplicationPipelineRecordController {

    @Resource
    private ApplicationPipelineRecordService applicationPipelineRecordService;

    @Resource
    private ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService;

    @Resource
    private TaskRegister taskRegister;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationPipelineRecordListVO> getPipelineRecordList(@RequestBody ApplicationPipelineRecordRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineRecordService.getPipelineRecordList(request);
    }

    /**
     * 明细详情
     */
    @RequestMapping("/record-details")
    public List<ApplicationPipelineDetailRecordVO> getRecordDetails(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineDetailRecordService.getRecordDetails(id);
    }

    /**
     * 提交
     */
    @RequestMapping("/submit")
    public Long submitPipelineExec(@RequestBody ApplicationPipelineRecordRequest request) {
        Valid.notNull(request.getPipelineId());
        Valid.notBlank(request.getTitle());
        List<ApplicationPipelineDetailRecordRequest> details = Valid.notEmpty(request.getDetails());
        for (ApplicationPipelineDetailRecordRequest detail : details) {
            Valid.notNull(detail.getId());
        }
        TimedType timedType = Valid.notNull(TimedType.of(request.getTimedExec()));
        if (TimedType.TIMED.equals(timedType)) {
            Date timedExecTime = Valid.notNull(request.getTimedExecTime());
            Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        }
        Long id = applicationPipelineRecordService.submitPipelineExec(request);
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
    public Integer auditPipeline(@RequestBody ApplicationPipelineRecordRequest request) {
        Valid.notNull(request.getId());
        AuditStatus status = Valid.notNull(AuditStatus.of(request.getAuditStatus()));
        if (AuditStatus.REJECT.equals(status)) {
            Valid.notBlank(request.getAuditReason());
        }
        return applicationPipelineRecordService.auditPipeline(request);
    }

    /**
     * 复制
     */
    @RequestMapping("/copy")
    public Long copyPipeline(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineRecordService.copyPipeline(id);
    }

    /**
     * 执行
     */
    @RequestMapping("/exec")
    public HttpWrapper<?> execPipeline(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineRecordService.execPipeline(id, false);
        return HttpWrapper.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer deletePipeline(@RequestBody ApplicationPipelineRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineRecordService.deletePipeline(idList);
    }

    /**
     * 设置定时
     */
    @RequestMapping("/set-timed")
    public HttpWrapper<?> setTimedExec(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        Date timedExecTime = Valid.notNull(request.getTimedExecTime());
        Valid.isTrue(timedExecTime.compareTo(new Date()) > 0, MessageConst.TIMED_GREATER_THAN_NOW);
        applicationPipelineRecordService.setPipelineTimedExec(id, timedExecTime);
        return HttpWrapper.ok();
    }

    /**
     * 取消定时
     */
    @RequestMapping("/cancel-timed")
    public HttpWrapper<?> cancelTimedExec(@RequestBody ApplicationPipelineRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationPipelineRecordService.cancelPipelineTimedExec(id);
        return HttpWrapper.ok();
    }

    // 详情
    // 状态
    // 停止
    // 日志

    // 操作详情
    // 部分停止
    // 跳过

    // 注入站内信
    // 注入操作日志

    // 恢复runner
    // 状态runner

}
