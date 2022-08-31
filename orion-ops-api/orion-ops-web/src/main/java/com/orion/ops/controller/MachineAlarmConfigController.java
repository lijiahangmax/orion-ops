package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.machine.MachineAlarmConfigRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import com.orion.ops.service.api.MachineAlarmConfigService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器报警配置 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 15:53
 */
@Api(tags = "机器报警配置")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-alarm-config")
public class MachineAlarmConfigController {

    @Resource
    private MachineAlarmConfigService machineAlarmConfigService;

    @GetMapping("/get")
    @ApiOperation(value = "获取报警配置")
    public MachineAlarmConfigWrapperVO getAlarmConfig(@RequestParam("machineId") Long machineId) {
        return machineAlarmConfigService.getAlarmConfig(machineId);
    }

    @PostMapping("/set-alarm")
    @ApiOperation(value = "设置报警配置")
    @EventLog(EventType.SET_MACHINE_ALARM_CONFIG)
    public HttpWrapper<?> setAlarmConfig(@RequestBody MachineAlarmConfigRequest request) {
        Valid.notNull(request.getMachineId());
        Valid.gte(request.getAlarmThreshold(), 0D, MessageConst.INVALID_PARAM);
        Valid.gte(request.getTriggerThreshold(), 0, MessageConst.INVALID_PARAM);
        Valid.gte(request.getNotifySilence(), 0, MessageConst.INVALID_PARAM);
        machineAlarmConfigService.setAlarmConfig(request);
        return HttpWrapper.ok();
    }

    @PostMapping("/set-group")
    @ApiOperation(value = "设置报警联系组")
    @EventLog(EventType.SET_MACHINE_ALARM_GROUP)
    public HttpWrapper<?> setAlarmGroup(@RequestBody MachineAlarmConfigRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        List<Long> groupIdList = Valid.notEmpty(request.getGroupIdList());
        machineAlarmConfigService.setAlarmGroup(machineId, groupIdList);
        return HttpWrapper.ok();
    }

}
