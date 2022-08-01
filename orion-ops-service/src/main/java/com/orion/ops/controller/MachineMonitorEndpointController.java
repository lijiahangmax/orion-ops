package com.orion.ops.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.service.api.MachineMonitorEndpointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 机器监控端点
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 14:22
 */
@Api(tags = "机器监控端点")
@RestController
@RestWrapper
@RequestMapping("/orion/api/monitor-endpoint")
public class MachineMonitorEndpointController {

    @Resource
    private MachineMonitorEndpointService machineMonitorEndpointService;

    @ApiOperation(value = "设置agent机器id")
    @GetMapping("/set-machine-id")
    public HttpWrapper<?> setMachineId(@RequestParam("machineId") Long machineId) {
        machineMonitorEndpointService.setMachineId(machineId);
        return HttpWrapper.ok();
    }

    @ApiOperation(value = "开始收集数据")
    @GetMapping("/start")
    public HttpWrapper<?> startCollect(@RequestParam("machineId") Long machineId) {
        machineMonitorEndpointService.startCollect(machineId);
        return HttpWrapper.ok();
    }

    @ApiOperation(value = "停止收集数据")
    @GetMapping("/stop")
    public HttpWrapper<?> stopCollect(@RequestParam("machineId") Long machineId) {
        machineMonitorEndpointService.stopCollect(machineId);
        return HttpWrapper.ok();
    }

    @ApiOperation(value = "获取机器基本指标")
    @GetMapping("/metrics")
    public JSONObject getBaseMetrics(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.getBaseMetrics(machineId);
    }

    @ApiOperation(value = "获取系统负载")
    @GetMapping("/load")
    public JSONObject getSystemLoad(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.getSystemLoad(machineId);
    }

    @ApiOperation(value = "获取top进程")
    @GetMapping("/top")
    public JSONArray getTopProcesses(@RequestParam("machineId") Long machineId, String name) {
        return machineMonitorEndpointService.getTopProcesses(machineId, name);
    }

}
