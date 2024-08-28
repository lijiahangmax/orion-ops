package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.app.RepositoryAuthType;
import com.orion.ops.constant.app.RepositoryTokenType;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.app.ApplicationRepositoryRequest;
import com.orion.ops.entity.vo.app.ApplicationRepositoryBranchVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryCommitVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryInfoVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryVO;
import com.orion.ops.service.api.ApplicationRepositoryService;
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
@RequestMapping("/orion/api/app-repo")
public class ApplicationRepositoryController {

    @Resource
    private ApplicationRepositoryService applicationRepositoryService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加版本仓库")
    @EventLog(EventType.ADD_REPOSITORY)
    public Long addRepository(@RequestBody ApplicationRepositoryRequest request) {
        Valid.allNotBlank(request.getName(), request.getUrl());
        RepositoryAuthType authType = Valid.notNull(RepositoryAuthType.of(request.getAuthType()));
        if (RepositoryAuthType.TOKEN.equals(authType)) {
            Valid.notNull(RepositoryTokenType.of(request.getTokenType()));
            Valid.notBlank(request.getPrivateToken());
        }
        return applicationRepositoryService.addRepository(request);
    }

    @DemoDisableApi
    @ApiOperation(value = "更新版本仓库")
    @PostMapping("/update")
    @EventLog(EventType.UPDATE_REPOSITORY)
    public Integer updateRepository(@RequestBody ApplicationRepositoryRequest request) {
        Valid.notNull(request.getId());
        Valid.allNotBlank(request.getName(), request.getUrl());
        return applicationRepositoryService.updateRepository(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除版本仓库")
    @EventLog(EventType.DELETE_REPOSITORY)
    public Integer deleteRepository(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationRepositoryService.deleteRepository(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取版本仓库列表")
    public DataGrid<ApplicationRepositoryVO> listRepository(@RequestBody ApplicationRepositoryRequest request) {
        return applicationRepositoryService.listRepository(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取版本仓库详情")
    public ApplicationRepositoryVO getRepositoryDetail(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationRepositoryService.getRepositoryDetail(id);
    }

    @PostMapping("/init")
    @ApiOperation(value = "初始化版本仓库")
    @EventLog(EventType.INIT_REPOSITORY)
    public HttpWrapper<?> initRepository(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationRepositoryService.initEventRepository(id, false);
        return HttpWrapper.ok();
    }

    @PostMapping("/re-init")
    @ApiOperation(value = "重新初始化版本仓库")
    @EventLog(EventType.RE_INIT_REPOSITORY)
    public HttpWrapper<?> reInitRepository(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationRepositoryService.initEventRepository(id, true);
        return HttpWrapper.ok();
    }

    @ApiOperation(value = "获取分支和提交记录列表")
    @PostMapping("/info")
    public ApplicationRepositoryInfoVO getRepositoryInfo(@RequestBody ApplicationRepositoryRequest request) {
        Valid.notNull(request.getId());
        return applicationRepositoryService.getRepositoryInfo(request);
    }

    @PostMapping("/branch")
    @ApiOperation(value = "获取分支列表")
    public List<ApplicationRepositoryBranchVO> getRepositoryBranchList(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationRepositoryService.getRepositoryBranchList(id);
    }

    @PostMapping("/commit")
    @ApiOperation(value = "获取提交列表")
    public List<ApplicationRepositoryCommitVO> getRepositoryCommitList(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        String branchName = Valid.notBlank(request.getBranchName());
        return applicationRepositoryService.getRepositoryCommitList(id, branchName);
    }

    @PostMapping("/clean")
    @ApiOperation(value = "清空应用构建历史版本")
    @EventLog(EventType.CLEAN_REPOSITORY)
    public HttpWrapper<?> cleanBuildRepository(@RequestBody ApplicationRepositoryRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationRepositoryService.cleanBuildRepository(id);
        return HttpWrapper.ok();
    }

}
