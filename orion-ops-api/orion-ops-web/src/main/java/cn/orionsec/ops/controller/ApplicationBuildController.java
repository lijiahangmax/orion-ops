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
package cn.orionsec.ops.controller;

import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.IgnoreLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.app.ApplicationBuildRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationBuildReleaseListVO;
import cn.orionsec.ops.entity.vo.app.ApplicationBuildStatusVO;
import cn.orionsec.ops.entity.vo.app.ApplicationBuildVO;
import cn.orionsec.ops.service.api.ApplicationBuildService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
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
