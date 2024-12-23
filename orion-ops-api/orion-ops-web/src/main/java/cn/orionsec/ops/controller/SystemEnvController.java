/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.kit.lang.define.collect.MutableLinkedHashMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.env.EnvViewType;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.system.SystemEnvRequest;
import cn.orionsec.ops.entity.vo.system.SystemEnvVO;
import cn.orionsec.ops.service.api.SystemEnvService;
import cn.orionsec.ops.utils.Valid;
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
 * 系统环境变量 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 20:40
 */
@Api(tags = "系统环境变量")
@RestController
@RestWrapper
@RequestMapping("/orion/api/system-env")
public class SystemEnvController {

    @Resource
    private SystemEnvService systemEnvService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加环境变量")
    @EventLog(EventType.ADD_SYSTEM_ENV)
    public Long add(@RequestBody SystemEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        return systemEnvService.addEnv(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改环境变量")
    @EventLog(EventType.UPDATE_SYSTEM_ENV)
    public Integer update(@RequestBody SystemEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return systemEnvService.updateEnv(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除环境变量")
    @EventLog(EventType.DELETE_SYSTEM_ENV)
    public Integer delete(@RequestBody SystemEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return systemEnvService.deleteEnv(idList);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取环境变量列表")
    public DataGrid<SystemEnvVO> list(@RequestBody SystemEnvRequest request) {
        return systemEnvService.listEnv(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取环境变量详情")
    public SystemEnvVO detail(@RequestBody SystemEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return systemEnvService.getEnvDetail(id);
    }

    @PostMapping("/view")
    @ApiOperation(value = "获取环境变量视图")
    public String view(@RequestBody SystemEnvRequest request) {
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        systemEnvService.listEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    @DemoDisableApi
    @PostMapping("/view-save")
    @ApiOperation(value = "保存环境变量视图")
    @EventLog(EventType.SAVE_SYSTEM_ENV)
    public Integer viewSave(@RequestBody SystemEnvRequest request) {
        String value = Valid.notBlank(request.getValue());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        try {
            MutableLinkedHashMap<String, String> result = viewType.toMap(value);
            systemEnvService.saveEnv(result);
            return result.size();
        } catch (Exception e) {
            throw Exceptions.argument(MessageConst.PARSE_ERROR, e);
        }
    }

}
