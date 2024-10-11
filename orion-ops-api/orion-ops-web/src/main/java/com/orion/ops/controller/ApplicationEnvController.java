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

import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.env.EnvViewType;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.app.ApplicationEnvRequest;
import com.orion.ops.entity.vo.app.ApplicationEnvVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 应用环境 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:10
 */
@Api(tags = "应用环境变量")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-env")
public class ApplicationEnvController {

    @Resource
    private ApplicationEnvService applicationEnvService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加环境变量")
    public Long addAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        Valid.notBlank(request.getKey());
        Valid.notBlank(request.getValue());
        return applicationEnvService.addAppEnv(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除环境变量")
    @EventLog(EventType.DELETE_APP_ENV)
    public Integer deleteAppEnv(@RequestBody ApplicationEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationEnvService.deleteAppEnv(idList);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "更新环境变量")
    public Integer updateAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getId());
        return applicationEnvService.updateAppEnv(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取环境变量列表")
    public DataGrid<ApplicationEnvVO> listAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        return applicationEnvService.listAppEnv(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取环境变量详情")
    public ApplicationEnvVO appEnvDetail(@RequestBody ApplicationEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationEnvService.getAppEnvDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/sync")
    @ApiOperation(value = "同步环境变量到其他环境")
    @EventLog(EventType.SYNC_APP_ENV)
    public HttpWrapper<?> syncAppEnv(@RequestBody ApplicationEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        List<Long> targetProfileIdList = Valid.notEmpty(request.getTargetProfileIdList());
        applicationEnvService.syncAppEnv(id, appId, profileId, targetProfileIdList);
        return HttpWrapper.ok();
    }

    @PostMapping("/view")
    @ApiOperation(value = "获取环境变量视图")
    public String view(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        applicationEnvService.listAppEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    @DemoDisableApi
    @PostMapping("/view-save")
    @ApiOperation(value = "保存环境变量视图")
    public Integer viewSave(@RequestBody ApplicationEnvRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        String value = Valid.notBlank(request.getValue());
        try {
            MutableLinkedHashMap<String, String> result = viewType.toMap(value);
            applicationEnvService.saveEnv(appId, profileId, result);
            return result.size();
        } catch (Exception e) {
            throw Exceptions.argument(MessageConst.PARSE_ERROR, e);
        }
    }

}
