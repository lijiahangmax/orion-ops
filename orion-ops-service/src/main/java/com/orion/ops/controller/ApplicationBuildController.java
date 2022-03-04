package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.ApplicationBuildRequest;
import com.orion.ops.entity.vo.ApplicationBuildReleaseListVO;
import com.orion.ops.entity.vo.ApplicationBuildStatusVO;
import com.orion.ops.entity.vo.ApplicationBuildVO;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用构建api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/6 22:02
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-build")
public class ApplicationBuildController {

    @Resource
    private ApplicationBuildService applicationBuildService;

    /**
     * 提交执行
     */
    @RequestMapping("/submit")
    @EventLog(EventType.SUBMIT_BUILD)
    public Long submitAppBuild(@RequestBody ApplicationBuildRequest request) {
        Valid.allNotNull(request.getAppId(), request.getProfileId());
        return applicationBuildService.submitBuildTask(request);
    }

    /**
     * 构建列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationBuildVO> getBuildList(@RequestBody ApplicationBuildRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationBuildService.getBuildList(request);
    }

    /**
     * 构建详情
     */
    @RequestMapping("/detail")
    public ApplicationBuildVO getBuildDetail(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.getBuildDetail(id);
    }

    /**
     * 查询构建状态
     */
    @RequestMapping("/status")
    public ApplicationBuildStatusVO getBuildStatus(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.getBuildStatus(id);
    }

    /**
     * 查询构建状态列表
     */
    @RequestMapping("/list/status")
    public List<ApplicationBuildStatusVO> getListStatus(@RequestBody ApplicationBuildRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationBuildService.getBuildStatusList(idList);
    }

    /**
     * 终止构建
     */
    @RequestMapping("/terminated")
    @EventLog(EventType.BUILD_TERMINATED)
    public HttpWrapper<?> terminatedTask(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        applicationBuildService.terminatedBuildTask(id);
        return HttpWrapper.ok();
    }

    /**
     * 删除构建
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_BUILD)
    public Integer deleteTask(@RequestBody ApplicationBuildRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationBuildService.deleteBuildTask(idList);
    }

    /**
     * 重新构建
     */
    @RequestMapping("/rebuild")
    @EventLog(EventType.SUBMIT_REBUILD)
    public Long rebuildTask(@RequestBody ApplicationBuildRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationBuildService.rebuild(id);
    }

    /**
     * 发布构建列表
     */
    @RequestMapping("/release/list")
    public List<ApplicationBuildReleaseListVO> getBuildReleaseList(@RequestBody ApplicationBuildRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationBuildService.getBuildReleaseList(appId, profileId);
    }

}
