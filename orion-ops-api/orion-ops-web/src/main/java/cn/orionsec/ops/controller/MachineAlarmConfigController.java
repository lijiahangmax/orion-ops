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

import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.machine.MachineAlarmConfigRequest;
import cn.orionsec.ops.entity.request.machine.MachineAlarmHistoryRequest;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmHistoryVO;
import cn.orionsec.ops.service.api.MachineAlarmConfigService;
import cn.orionsec.ops.service.api.MachineAlarmHistoryService;
import cn.orionsec.ops.service.api.MachineAlarmService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
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
