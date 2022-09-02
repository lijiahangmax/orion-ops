package com.orion.ops.expose;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.monitor.MonitorStatus;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.service.api.MachineMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 机器监控 暴露api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/1 17:52
 */
@Api(tags = "暴露服务-机器监控")
@RestController
@RestWrapper
@RequestMapping("/orion/expose-api/machine-monitor")
public class MachineMonitorExposeController {

    @Resource
    private MachineMonitorService machineMonitorService;

    @GetMapping("/started")
    @ApiOperation(value = "监控启动回调")
    public Integer getAlarmConfig(@RequestParam("machineId") Long machineId, @RequestParam("version") String version) {
        MachineMonitorDO update = new MachineMonitorDO();
        update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
        update.setAgentVersion(version);
        return machineMonitorService.updateMonitorConfigByMachineId(machineId, update);
    }

}
