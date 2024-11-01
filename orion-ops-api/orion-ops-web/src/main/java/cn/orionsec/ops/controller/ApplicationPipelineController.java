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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineDetailRequest;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineVO;
import cn.orionsec.ops.service.api.ApplicationPipelineService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用流水线 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:17
 */
@Api(tags = "应用流水线")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-pipeline")
public class ApplicationPipelineController {

    @Resource
    private ApplicationPipelineService applicationPipelineService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "新增应用流水线")
    @EventLog(EventType.ADD_PIPELINE)
    public Long addPipeline(@RequestBody ApplicationPipelineRequest request) {
        this.validParams(request);
        return applicationPipelineService.addPipeline(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改应用流水线")
    @EventLog(EventType.UPDATE_PIPELINE)
    public Integer updatePipeline(@RequestBody ApplicationPipelineRequest request) {
        Valid.notNull(request.getId());
        this.validParams(request);
        return applicationPipelineService.updatePipeline(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取应用流水线列表")
    public DataGrid<ApplicationPipelineVO> listPipeline(@RequestBody ApplicationPipelineRequest request) {
        Valid.notNull(request.getProfileId());
        return applicationPipelineService.listPipeline(request);
    }

    @PostMapping("/get")
    @ApiOperation(value = "详情获取应用流水线")
    public ApplicationPipelineVO getPipeline(@RequestBody ApplicationPipelineRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationPipelineService.getPipeline(id);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除应用流水线")
    @EventLog(EventType.DELETE_PIPELINE)
    public Integer deletePipeline(@RequestBody ApplicationPipelineRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationPipelineService.deletePipeline(idList);
    }

    /**
     * 检查参数
     *
     * @param request request
     */
    private void validParams(@RequestBody ApplicationPipelineRequest request) {
        Valid.notBlank(request.getName());
        Valid.notNull(request.getProfileId());
        List<ApplicationPipelineDetailRequest> details = Valid.notEmpty(request.getDetails());
        for (ApplicationPipelineDetailRequest detail : details) {
            Valid.notNull(detail.getAppId());
            Valid.notNull(StageType.of(detail.getStageType()));
        }
    }

}
