package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.app.ApplicationBuildRequest;
import com.orion.ops.entity.vo.app.ApplicationBuildReleaseListVO;
import com.orion.ops.entity.vo.app.ApplicationBuildStatusVO;
import com.orion.ops.entity.vo.app.ApplicationBuildVO;
import com.orion.ops.service.api.ApplicationBuildService;
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
 * 应用构建 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/6 22:02
 */
@Api(tags = "应用构建")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-build")
public class ApplicationBuildController {

    @Resource
    private ApplicationBuildService applicationBuildService;

    @PostMapping("/submit")
    @ApiOperation(value = "提交执行")
    @EventLog(EventType.SUBMIT_BUILD)
    public Long submitAppBuild(@RequestBody ApplicationBuildRequest request) {
        Valid.allNotNull(request.getAppId(), request.getProfileId());
        return applicationBuildService.submitBuildTask(request, true);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取构建列表")
    public DataGrid<ApplicationBuildVO> getBuildList(@RequestBody ApplicationBuildRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationBuildService.getBuildList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取构建详情")
    public ApplicationBuildVO getBuildDetail(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.getBuildDetail(id);
    }

    @IgnoreLog
    @PostMapping("/status")
    @ApiOperation(value = "查询构建状态")
    public ApplicationBuildStatusVO getBuildStatus(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.getBuildStatus(id);
    }

    @IgnoreLog
    @PostMapping("/list-status")
    @ApiOperation(value = "查询构建状态列表")
    public List<ApplicationBuildStatusVO> getListStatus(@RequestBody ApplicationBuildRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationBuildService.getBuildStatusList(idList);
    }

    @PostMapping("/terminate")
    @ApiOperation(value = "终止构建")
    @EventLog(EventType.BUILD_TERMINATE)
    public HttpWrapper<?> terminateTask(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationBuildService.terminateBuildTask(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/write")
    @ApiOperation(value = "输入命令")
    public HttpWrapper<?> writeTask(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        String command = Valid.notEmpty(request.getCommand());
        applicationBuildService.writeBuildTask(id, command);
        return HttpWrapper.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除构建")
    @EventLog(EventType.DELETE_BUILD)
    public Integer deleteTask(@RequestBody ApplicationBuildRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationBuildService.deleteBuildTask(idList);
    }

    @PostMapping("/rebuild")
    @ApiOperation(value = "删除构建")
    @EventLog(EventType.SUBMIT_REBUILD)
    public Long rebuildTask(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.rebuild(id);
    }

    @PostMapping("/release-list")
    @ApiOperation(value = "获取发布构建列表")
    public List<ApplicationBuildReleaseListVO> getBuildReleaseList(@RequestBody ApplicationBuildRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationBuildService.getBuildReleaseList(appId, profileId);
    }

}
