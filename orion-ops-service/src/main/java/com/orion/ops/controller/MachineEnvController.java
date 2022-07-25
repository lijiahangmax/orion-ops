package com.orion.ops.controller;

import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.env.EnvViewType;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.MachineEnvService;
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

    @PostMapping("/add")
    @ApiOperation(value = "添加环境变量")
    public Long add(@RequestBody MachineEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        Valid.notNull(request.getMachineId());
        return machineEnvService.addEnv(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改环境变量")
    public Integer update(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return machineEnvService.updateEnv(request);
    }

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
