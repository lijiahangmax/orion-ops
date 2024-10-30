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

import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.app.RepositoryAuthType;
import cn.orionsec.ops.constant.app.RepositoryTokenType;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.app.ApplicationRepositoryRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationRepositoryBranchVO;
import cn.orionsec.ops.entity.vo.app.ApplicationRepositoryCommitVO;
import cn.orionsec.ops.entity.vo.app.ApplicationRepositoryInfoVO;
import cn.orionsec.ops.entity.vo.app.ApplicationRepositoryVO;
import cn.orionsec.ops.service.api.ApplicationRepositoryService;
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
