package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.machine.MachineAlarmConfigRequest;
import com.orion.ops.entity.request.machine.MachineAlarmHistoryRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import com.orion.ops.entity.vo.machine.MachineAlarmHistoryVO;
import com.orion.ops.service.api.MachineAlarmConfigService;
import com.orion.ops.service.api.MachineAlarmHistoryService;
import com.orion.ops.service.api.MachineAlarmService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器报警 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 15:53
 */
@Api(tags = "机器报警")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-alarm")
public class MachineAlarmConfigController {

    @Resource
    private MachineAlarmConfigService machineAlarmConfigService;

    @Resource
    private MachineAlarmHistoryService machineAlarmHistoryService;

    @Resource
    private MachineAlarmService machineAlarmService;

    @GetMapping("/get-config")
    @ApiOperation(value = "获取报警配置")
    public MachineAlarmConfigWrapperVO getAlarmConfig(@RequestParam("machineId") Long machineId) {
        return machineAlarmConfigService.getAlarmConfigInfo(machineId);
    }

    @PostMapping("/set-alarm-config")
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

    @PostMapping("/set-group-config")
    @ApiOperation(value = "设置报警联系组")
    @EventLog(EventType.SET_MACHINE_ALARM_GROUP)
    public HttpWrapper<?> setAlarmGroup(@RequestBody MachineAlarmConfigRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        List<Long> groupIdList = Valid.notEmpty(request.getGroupIdList());
        machineAlarmConfigService.setAlarmGroup(machineId, groupIdList);
        return HttpWrapper.ok();
    }

    @PostMapping("/history")
    @ApiOperation(value = "获取报警历史")
    public DataGrid<MachineAlarmHistoryVO> getAlarmHistory(@RequestBody MachineAlarmHistoryRequest request) {
        Valid.notNull(request.getMachineId());
        return machineAlarmHistoryService.getAlarmHistory(request);
    }

    @PostMapping("/trigger-alarm-notify")
    @ApiOperation(value = "触发报警通知")
    @EventLog(EventType.RENOTIFY_MACHINE_ALARM_GROUP)
    public HttpWrapper<?> triggerMachineAlarm(@RequestBody MachineAlarmHistoryRequest request) {
        Long id = Valid.notNull(request.getId());
        machineAlarmService.triggerMachineAlarm(id);
        return HttpWrapper.ok();
    }

}
