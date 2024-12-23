/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.IgnoreLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.scheduler.SchedulerTaskRecordRequest;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordVO;
import cn.orionsec.ops.service.api.SchedulerTaskRecordService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 调度任务执行明细 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:32
 */
@Api(tags = "调度任务执行明细")
@RestController
@RestWrapper
@RequestMapping("/orion/api/scheduler-record")
public class SchedulerRecordController {

    @Resource
    private SchedulerTaskRecordService schedulerTaskRecordService;

    @PostMapping("/list")
    @ApiOperation(value = "获取调度任务执行列表")
    public DataGrid<SchedulerTaskRecordVO> listRecord(@RequestBody SchedulerTaskRecordRequest request) {
        return schedulerTaskRecordService.listTaskRecord(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取调度任务执行详情")
    public SchedulerTaskRecordVO getRecordDetail(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskRecordService.getDetailById(id);
    }

    @PostMapping("/machines")
    @ApiOperation(value = "查询调度任务执行机器")
    public List<SchedulerTaskMachineRecordVO> listMachinesRecord(@RequestBody SchedulerTaskRecordRequest request) {
        Long recordId = Valid.notNull(request.getRecordId());
        return schedulerTaskRecordService.listMachinesRecord(recordId);
    }

    @IgnoreLog
    @PostMapping("/list-status")
    @ApiOperation(value = "查询调度任务执行状态")
    public List<SchedulerTaskRecordStatusVO> listRecordStatus(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.listRecordStatus(idList, request.getMachineRecordIdList());
    }

    @IgnoreLog
    @PostMapping("/machines-status")
    @ApiOperation(value = "查询调度任务机器执行状态")
    public List<SchedulerTaskMachineRecordStatusVO> listMachineRecordStatus(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.listMachineRecordStatus(idList);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除调度任务明细")
    @EventLog(EventType.DELETE_TASK_RECORD)
    public Integer deleteRecord(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.deleteTaskRecord(idList);
    }

    @PostMapping("/terminate-all")
    @ApiOperation(value = "停止执行调度任务")
    @EventLog(EventType.TERMINATE_ALL_SCHEDULER_TASK)
    public HttpWrapper<?> terminateAll(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        schedulerTaskRecordService.terminateAll(id);
        return HttpWrapper.ok();
    }

    @PostMapping("/terminate-machine")
    @ApiOperation(value = "停止单台机器执行调度任务")
    @EventLog(EventType.TERMINATE_SCHEDULER_TASK_MACHINE)
    public HttpWrapper<?> terminateMachine(@RequestBody SchedulerTaskRecordRequest request) {
        Long machineRecordId = Valid.notNull(request.getMachineRecordId());
        schedulerTaskRecordService.terminateMachine(machineRecordId);
        return HttpWrapper.ok();
    }

    @PostMapping("/skip-machine")
    @ApiOperation(value = "跳过单台机器执行调度任务")
    @EventLog(EventType.SKIP_SCHEDULER_TASK_MACHINE)
    public HttpWrapper<?> skipMachine(@RequestBody SchedulerTaskRecordRequest request) {
        Long machineRecordId = Valid.notNull(request.getMachineRecordId());
        schedulerTaskRecordService.skipMachine(machineRecordId);
        return HttpWrapper.ok();
    }

    @PostMapping("/write-machine")
    @ApiOperation(value = "写入命令")
    public HttpWrapper<?> writeMachine(@RequestBody SchedulerTaskRecordRequest request) {
        Long machineRecordId = Valid.notNull(request.getMachineRecordId());
        String command = Valid.notEmpty(request.getCommand());
        schedulerTaskRecordService.writeMachine(machineRecordId, command);
        return HttpWrapper.ok();
    }

}
