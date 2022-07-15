package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.app.VcsAuthType;
import com.orion.ops.consts.app.VcsTokenType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.ApplicationVcsRequest;
import com.orion.ops.entity.vo.ApplicationVcsBranchVO;
import com.orion.ops.entity.vo.ApplicationVcsCommitVO;
import com.orion.ops.entity.vo.ApplicationVcsInfoVO;
import com.orion.ops.entity.vo.ApplicationVcsVO;
import com.orion.ops.service.api.ApplicationVcsService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用版本仓库 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/29 8:55
 */
@Api(tags = "应用版本仓库")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-vcs")
public class ApplicationVcsController {

    @Resource
    private ApplicationVcsService applicationVcsService;

    @PostMapping("/add")
    @ApiOperation(value = "添加版本仓库")
    @EventLog(EventType.ADD_VCS)
    public Long addAppVcs(@RequestBody ApplicationVcsRequest request) {
        Valid.allNotBlank(request.getName(), request.getUrl());
        VcsAuthType authType = Valid.notNull(VcsAuthType.of(request.getAuthType()));
        if (VcsAuthType.TOKEN.equals(authType)) {
            Valid.notNull(VcsTokenType.of(request.getTokenType()));
            Valid.notBlank(request.getPrivateToken());
        }
        return applicationVcsService.addAppVcs(request);
    }

    @ApiOperation(value = "更新版本仓库")
    @PostMapping("/update")
    @EventLog(EventType.UPDATE_VCS)
    public Integer updateAppVcs(@RequestBody ApplicationVcsRequest request) {
        Valid.notNull(request.getId());
        Valid.allNotBlank(request.getName(), request.getUrl());
        return applicationVcsService.updateAppVcs(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除版本仓库")
    @EventLog(EventType.DELETE_VCS)
    public Integer deleteAppVcs(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationVcsService.deleteAppVcs(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取版本仓库列表")
    public DataGrid<ApplicationVcsVO> listAppVcs(@RequestBody ApplicationVcsRequest request) {
        return applicationVcsService.listAppVcs(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取版本仓库详情")
    public ApplicationVcsVO getAppVcsDetail(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationVcsService.getAppVcsDetail(id);
    }

    @PostMapping("/init")
    @ApiOperation(value = "初始化版本仓库")
    @EventLog(EventType.INIT_VCS)
    public HttpWrapper<?> initVcs(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationVcsService.initEventVcs(id, false);
        return HttpWrapper.ok();
    }

    @PostMapping("/re-init")
    @ApiOperation(value = "重新初始化版本仓库")
    @EventLog(EventType.RE_INIT_VCS)
    public HttpWrapper<?> reInitVcs(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationVcsService.initEventVcs(id, true);
        return HttpWrapper.ok();
    }

    @ApiOperation(value = "获取分支和提交记录列表")
    @PostMapping("/info")
    public ApplicationVcsInfoVO getVcsInfo(@RequestBody ApplicationVcsRequest request) {
        Valid.notNull(request.getId());
        return applicationVcsService.getVcsInfo(request);
    }

    @PostMapping("/branch")
    @ApiOperation(value = "获取分支列表")
    public List<ApplicationVcsBranchVO> getVcsBranchList(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationVcsService.getVcsBranchList(id);
    }

    @PostMapping("/commit")
    @ApiOperation(value = "获取提交列表")
    public List<ApplicationVcsCommitVO> getVcsCommitList(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        String branchName = Valid.notBlank(request.getBranchName());
        return applicationVcsService.getVcsCommitList(id, branchName);
    }

    @PostMapping("/clean")
    @ApiOperation(value = "清空应用构建历史版本")
    @EventLog(EventType.CLEAN_VCS)
    public HttpWrapper<?> cleanBuildVcs(@RequestBody ApplicationVcsRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationVcsService.cleanBuildVcs(id);
        return HttpWrapper.ok();
    }

}
