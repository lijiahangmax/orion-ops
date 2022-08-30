package com.orion.ops.expose;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.machine.MachineAlarmType;
import com.orion.ops.entity.request.machine.MachineAlarmRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import com.orion.ops.service.api.MachineAlarmConfigService;
import com.orion.ops.service.api.MachineAlarmService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 机器报警 暴露api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 16:32
 */
@Api(tags = "暴露服务-机器报警")
@RestController
@RestWrapper
@RequestMapping("/orion/expose-api/machine-alarm")
public class MachineAlarmExposeController {

    @Resource
    private MachineAlarmConfigService machineAlarmConfigService;

    @Resource
    private MachineAlarmService machineAlarmService;

    @GetMapping("/get-config")
    @ApiOperation(value = "获取报警配置")
    public MachineAlarmConfigWrapperVO getAlarmConfig(@RequestParam("machineId") Long machineId) {
        return machineAlarmConfigService.getAlarmConfig(machineId);
    }

    @PostMapping("/trigger-alarm")
    @ApiOperation(value = "触发机器报警")
    public HttpWrapper<?> triggerMachineAlarm(@RequestBody MachineAlarmRequest request) {
        Valid.allNotNull(request.getMachineId(), request.getAlarmTime(),
                request.getAlarmValue(), MachineAlarmType.of(request.getType()));
        machineAlarmService.triggerMachineAlarm(request);
        return HttpWrapper.ok();
    }

}
