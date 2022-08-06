package com.orion.ops.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.MachineMonitorEndpointRequest;
import com.orion.ops.service.api.MachineMonitorEndpointService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/ping")
    @ApiOperation(value = "ping")
    public Integer sendPing(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.ping(machineId);
    }

    @GetMapping("/metrics")
    @ApiOperation(value = "获取机器基本指标")
    public JSONObject getBaseMetrics(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.getBaseMetrics(machineId);
    }

    @GetMapping("/load")
    @ApiOperation(value = "获取系统负载")
    public JSONObject getSystemLoad(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.getSystemLoad(machineId);
    }

    @GetMapping("/top")
    @ApiOperation(value = "获取top进程")
    public JSONArray getTopProcesses(@RequestParam("machineId") Long machineId, String name) {
        return machineMonitorEndpointService.getTopProcesses(machineId, name);
    }

    @GetMapping("/disk-name")
    @ApiOperation(value = "获取磁盘名称")
    public JSONArray getCpuStatistics(@RequestParam("machineId") Long machineId) {
        return machineMonitorEndpointService.getDiskName(machineId);
    }

    @PostMapping("/chart-cpu")
    @ApiOperation(value = "获取cpu图表")
    public JSONObject getCpuStatistics(@RequestBody MachineMonitorEndpointRequest request) {
        this.validChartParams(request);
        return machineMonitorEndpointService.getCpuChart(request);
    }

    @PostMapping("/chart-memory")
    @ApiOperation(value = "获取内存图表")
    public JSONObject getMemoryStatistics(@RequestBody MachineMonitorEndpointRequest request) {
        this.validChartParams(request);
        return machineMonitorEndpointService.getMemoryChart(request);
    }

    @PostMapping("/chart-net")
    @ApiOperation(value = "获取网络图表")
    public JSONObject getNetStatistics(@RequestBody MachineMonitorEndpointRequest request) {
        this.validChartParams(request);
        return machineMonitorEndpointService.getNetChart(request);
    }

    @PostMapping("/chart-disk")
    @ApiOperation(value = "获取磁盘图表")
    public JSONObject getDiskStatistics(@RequestBody MachineMonitorEndpointRequest request) {
        this.validChartParams(request);
        return machineMonitorEndpointService.getDiskChart(request);
    }

    /**
     * 验证参数
     *
     * @param request request
     */
    private void validChartParams(MachineMonitorEndpointRequest request) {
        Valid.notNull(request.getMachineId());
        Valid.notNull(request.getGranularity());
        Valid.notNull(request.getStartRange());
        Valid.notNull(request.getEndRange());
    }

}
