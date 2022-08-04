package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.MachineMonitorRequest;
import com.orion.ops.entity.vo.MachineMonitorVO;
import com.orion.ops.service.api.MachineMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 机器监控
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/2 14:34
 */
@Api(tags = "机器监控")
@RestController
@RestWrapper
@RequestMapping("/orion/api/monitor")
public class MachineMonitorController {

    @Resource
    private MachineMonitorService machineMonitorService;

    @PostMapping("/list")
    @ApiOperation(value = "查询监控列表")
    public DataGrid<MachineMonitorVO> getMonitorList(@RequestBody MachineMonitorRequest request) {
        return machineMonitorService.getMonitorList(request);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "查询监控配置")
    public MachineMonitorVO getMonitorDetail(@RequestParam Long machineId) {
        return machineMonitorService.getMonitorConfig(machineId);
    }


}
