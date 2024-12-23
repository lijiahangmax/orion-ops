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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.machine.ProxyType;
import cn.orionsec.ops.entity.request.machine.MachineProxyRequest;
import cn.orionsec.ops.entity.vo.machine.MachineProxyVO;
import cn.orionsec.ops.service.api.MachineProxyService;
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
 * 机器代理 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:11
 */
@Api(tags = "机器代理")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-proxy")
public class MachineProxyController {

    @Resource
    private MachineProxyService machineProxyService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加机器代理")
    @EventLog(EventType.ADD_MACHINE_PROXY)
    public Long addProxy(@RequestBody MachineProxyRequest request) {
        request.setId(null);
        this.check(request);
        if (!Strings.isBlank(request.getUsername())) {
            Valid.notNull(request.getPassword());
        }
        return machineProxyService.addProxy(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改机器代理")
    @EventLog(EventType.UPDATE_MACHINE_PROXY)
    public Integer update(@RequestBody MachineProxyRequest request) {
        Valid.notNull(request.getId());
        this.check(request);
        return machineProxyService.updateProxy(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取机器代理列表")
    public DataGrid<MachineProxyVO> list(@RequestBody MachineProxyRequest request) {
        return machineProxyService.listProxy(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取机器代理详情")
    public MachineProxyVO detail(@RequestBody MachineProxyRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineProxyService.getProxyDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除机器代理")
    @EventLog(EventType.DELETE_MACHINE_PROXY)
    public Integer delete(@RequestBody MachineProxyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineProxyService.deleteProxy(idList);
    }

    /**
     * 合法校验
     */
    private void check(MachineProxyRequest request) {
        Valid.notBlank(request.getHost());
        Valid.notNull(request.getPort());
        Valid.notNull(ProxyType.of(request.getType()));
    }

}
