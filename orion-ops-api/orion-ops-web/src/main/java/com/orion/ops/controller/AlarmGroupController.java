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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.alarm.AlarmGroupRequest;
import com.orion.ops.entity.vo.alarm.AlarmGroupVO;
import com.orion.ops.service.api.AlarmGroupService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报警组配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 11:15
 */
@Api(tags = "报警组配置")
@RestController
@RestWrapper
@RequestMapping("/orion/api/alarm-group")
public class AlarmGroupController {

    @Resource
    private AlarmGroupService alarmGroupService;

    @PostMapping("/add")
    @ApiOperation(value = "添加报警组")
    @EventLog(EventType.ADD_ALARM_GROUP)
    public Long addAlarmGroup(@RequestBody AlarmGroupRequest request) {
        this.validParams(request);
        return alarmGroupService.addAlarmGroup(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新报警组")
    @EventLog(EventType.UPDATE_ALARM_GROUP)
    public Integer updateAlarmGroup(@RequestBody AlarmGroupRequest request) {
        Valid.notNull(request.getId());
        this.validParams(request);
        return alarmGroupService.updateAlarmGroup(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除报警组")
    @EventLog(EventType.DELETE_ALARM_GROUP)
    public Integer deleteAlarmGroup(@RequestBody AlarmGroupRequest request) {
        Long id = Valid.notNull(request.getId());
        return alarmGroupService.deleteAlarmGroup(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询列表")
    public DataGrid<AlarmGroupVO> getAlarmGroupList(@RequestBody AlarmGroupRequest request) {
        return alarmGroupService.getAlarmGroupList(request);
    }

    @PostMapping("/get")
    @ApiOperation(value = "查询详情")
    public AlarmGroupVO getAlarmGroupDetail(@RequestBody AlarmGroupRequest request) {
        Long id = Valid.notNull(request.getId());
        return alarmGroupService.getAlarmGroupDetail(id);
    }

    /**
     * 验证参数
     *
     * @param request request
     */
    private void validParams(AlarmGroupRequest request) {
        Valid.notBlank(request.getName());
        Valid.notEmpty(request.getUserIdList());
        Valid.notEmpty(request.getNotifyIdList());
    }

}
