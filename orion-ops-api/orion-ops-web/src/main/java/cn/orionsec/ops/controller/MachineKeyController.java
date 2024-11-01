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
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.machine.MachineKeyRequest;
import cn.orionsec.ops.entity.vo.machine.MachineSecretKeyVO;
import cn.orionsec.ops.service.api.MachineKeyService;
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
 * 机器密钥 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 23:10
 */
@Api(tags = "机器密钥")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-key")
public class MachineKeyController {

    @Resource
    private MachineKeyService machineKeyService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加机器密钥")
    @EventLog(EventType.ADD_MACHINE_KEY)
    public Long addKey(@RequestBody MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getFile());
        return machineKeyService.addSecretKey(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "更新机器密钥")
    @EventLog(EventType.UPDATE_MACHINE_KEY)
    public Integer updateKey(@RequestBody MachineKeyRequest request) {
        Valid.notNull(request.getId());
        return machineKeyService.updateSecretKey(request);
    }

    @DemoDisableApi
    @PostMapping("/remove")
    @ApiOperation(value = "删除机器密钥")
    @EventLog(EventType.DELETE_MACHINE_KEY)
    public Integer removeKey(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineKeyService.removeSecretKey(idList);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取机器密钥列表")
    public DataGrid<MachineSecretKeyVO> listKeys(@RequestBody MachineKeyRequest request) {
        return machineKeyService.listKeys(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取机器密钥详情")
    public MachineSecretKeyVO getKeyDetail(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineKeyService.getKeyDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/bind")
    @ApiOperation(value = "绑定机器密钥")
    @EventLog(EventType.BIND_MACHINE_KEY)
    public HttpWrapper<?> bindMachineKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        List<Long> machineIdList = Valid.notEmpty(request.getMachineIdList());
        machineKeyService.bindMachineKey(id, machineIdList);
        return HttpWrapper.ok();
    }

}
