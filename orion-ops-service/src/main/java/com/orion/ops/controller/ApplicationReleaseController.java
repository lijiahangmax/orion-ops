package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.RoleType;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.request.ApplicationReleaseLogRequest;
import com.orion.ops.entity.request.ApplicationReleaseSubmitRequest;
import com.orion.ops.entity.vo.ReleaseBillDetailVO;
import com.orion.ops.entity.vo.ReleaseBillListVO;
import com.orion.ops.service.api.ApplicationReleaseService;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用上线单api
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
    private ReleaseInfoService releaseInfoService;

    /**
     * 提交上线单
     */
    @RequestMapping("/submit")
    public Long submitReleaseBill(@RequestBody ApplicationReleaseSubmitRequest request) {
        Valid.notBlank(request.getTitle());
        Valid.notBlank(request.getDescription());
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        Valid.notBlank(request.getBranchName());
        Valid.notBlank(request.getCommitId());
        Valid.notBlank(request.getCommitMessage());
        Valid.notEmpty(request.getMachineIdList());
        return applicationReleaseService.submitAppRelease(request);
    }

    // copy

    /**
     * 审核上线单
     */
    @RequestMapping("/audit")
    @RequireRole(RoleType.ADMINISTRATOR)
    public Integer submitReleaseBill(@RequestBody ApplicationReleaseAuditRequest request) {
        Valid.notNull(request.getId());
        AuditStatus status = Valid.notNull(AuditStatus.of(request.getStatus()));
        if (AuditStatus.REJECT.equals(status)) {
            Valid.notBlank(request.getReason());
        }
        return applicationReleaseService.auditAppRelease(request);
    }

    /**
     * 运行上线单
     */
    @RequestMapping("/runnable")
    @IgnoreWrapper
    public HttpWrapper<?> runnableReleaseBill(@RequestBody ApplicationReleaseBillRequest request) {
        Valid.notNull(request.getId());
        applicationReleaseService.runnableAppRelease(request);
        return HttpWrapper.ok();
    }

    /**
     * 上线单列表
     */
    @RequestMapping("/list")
    public DataGrid<ReleaseBillListVO> releaseBillList(@RequestBody ApplicationReleaseBillRequest request) {
        return releaseInfoService.releaseBillList(request);
    }

    /**
     * 上线单详情
     */
    @RequestMapping("/detail")
    public ReleaseBillDetailVO releaseBillDetail(@RequestBody ApplicationReleaseBillRequest request) {
        Long id = Valid.notNull(request.getId());
        return releaseInfoService.releaseBillDetail(id);
    }

    /**
     * 上线单宿主机日志
     */
    @RequestMapping("/target/log")
    public String releaseTargetLog(@RequestBody ApplicationReleaseLogRequest request) {
        Long releaseId = Valid.notNull(request.getReleaseId());
        return releaseInfoService.releaseTargetLog(releaseId);
    }

    /**
     * 上线单目标机器日志
     */
    @RequestMapping("/machine/log")
    public String releaseMachineLog(@RequestBody ApplicationReleaseLogRequest request) {
        Long releaseMachineId = Valid.notNull(request.getReleaseMachineId());
        return releaseInfoService.releaseMachineLog(releaseMachineId);
    }

}
