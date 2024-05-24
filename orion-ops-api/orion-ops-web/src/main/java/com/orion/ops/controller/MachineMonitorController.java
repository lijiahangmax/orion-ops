package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Booleans;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.machine.MachineMonitorRequest;
import com.orion.ops.entity.vo.machine.MachineMonitorVO;
import com.orion.ops.service.api.MachineMonitorService;
import com.orion.ops.utils.Valid;
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

    @GetMapping("/get-config")
    @ApiOperation(value = "查询监控配置")
    public MachineMonitorVO getMonitorConfig(@RequestParam Long machineId) {
        return machineMonitorService.getMonitorConfig(machineId);
    }

    @PostMapping("/set-config")
    @ApiOperation(value = "设置监控配置")
    @EventLog(EventType.UPDATE_MACHINE_MONITOR_CONFIG)
    public MachineMonitorVO setMonitorConfig(@RequestBody MachineMonitorRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getUrl());
        Valid.notBlank(request.getAccessToken());
        return machineMonitorService.updateMonitorConfig(request);
    }

    @PostMapping("/test")
    @ApiOperation(value = "测试连接监控")
    public String testConnect(@RequestBody MachineMonitorRequest request) {
        String url = Valid.notBlank(request.getUrl());
        String accessToken = Valid.notBlank(request.getAccessToken());
        return machineMonitorService.getMonitorVersion(url, accessToken);
    }

    @DemoDisableApi
    @PostMapping("/install")
    @ApiOperation(value = "安装监控插件")
    @EventLog(EventType.INSTALL_UPGRADE_MACHINE_MONITOR)
    public MachineMonitorVO installMonitorAgent(@RequestBody MachineMonitorRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return machineMonitorService.installMonitorAgent(machineId, Booleans.isTrue(request.getUpgrade()));
    }

    @PostMapping("/check")
    @ApiOperation(value = "检查监控插件状态")
    public MachineMonitorVO checkMonitorStatus(@RequestBody MachineMonitorRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return machineMonitorService.checkMonitorStatus(machineId);
    }

}
