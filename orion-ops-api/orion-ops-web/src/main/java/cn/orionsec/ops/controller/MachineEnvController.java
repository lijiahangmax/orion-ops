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
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.env.EnvViewType;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.machine.MachineEnvRequest;
import cn.orionsec.ops.entity.vo.machine.MachineEnvVO;
import cn.orionsec.ops.service.api.MachineEnvService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Maps;
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
 * 机器环境变量 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 10:06
 */
@Api(tags = "机器环境变量")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-env")
public class MachineEnvController {

    @Resource
    private MachineEnvService machineEnvService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加环境变量")
    public Long add(@RequestBody MachineEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        Valid.notNull(request.getMachineId());
        return machineEnvService.addEnv(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改环境变量")
    public Integer update(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return machineEnvService.updateEnv(request);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除环境变量")
    @EventLog(EventType.DELETE_MACHINE_ENV)
    public Integer delete(@RequestBody MachineEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineEnvService.deleteEnv(idList);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取环境变量列表")
    public DataGrid<MachineEnvVO> list(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getMachineId());
        return machineEnvService.listEnv(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取环境变量详情")
    public MachineEnvVO detail(@RequestBody MachineEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineEnvService.getEnvDetail(id);
    }

    @DemoDisableApi
    @PostMapping("/sync")
    @ApiOperation(value = "同步环境变量")
    @EventLog(EventType.SYNC_MACHINE_ENV)
    public HttpWrapper<?> sync(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getMachineId());
        Valid.notEmpty(request.getTargetMachineIdList());
        machineEnvService.syncMachineEnv(request);
        return HttpWrapper.ok();
    }

    @PostMapping("/view")
    @ApiOperation(value = "获取环境变量视图")
    public String view(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getMachineId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        machineEnvService.listEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    @DemoDisableApi
    @PostMapping("/view-save")
    @ApiOperation(value = "保存环境变量视图")
    public Integer viewSave(@RequestBody MachineEnvRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        String value = Valid.notBlank(request.getValue());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        try {
            MutableLinkedHashMap<String, String> result = viewType.toMap(value);
            machineEnvService.saveEnv(machineId, result);
            return result.size();
        } catch (Exception e) {
            throw Exceptions.argument(MessageConst.PARSE_ERROR, e);
        }
    }

}
