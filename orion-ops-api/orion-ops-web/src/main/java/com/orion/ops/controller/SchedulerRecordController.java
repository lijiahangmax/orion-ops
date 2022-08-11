package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.scheduler.SchedulerTaskRecordRequest;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskMachineRecordStatusVO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskMachineRecordVO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskRecordStatusVO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskRecordVO;
import com.orion.ops.service.api.SchedulerTaskRecordService;
import com.orion.ops.utils.Valid;
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
