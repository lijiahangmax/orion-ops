package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.SchedulerTaskRecordRequest;
import com.orion.ops.entity.vo.SchedulerTaskMachineRecordStatusVO;
import com.orion.ops.entity.vo.SchedulerTaskMachineRecordVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordStatusVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordVO;
import com.orion.ops.service.api.SchedulerTaskRecordService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 调度任务明细api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:32
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/scheduler-record")
public class SchedulerRecordController {

    @Resource
    private SchedulerTaskRecordService schedulerTaskRecordService;

    /**
     * 查询调度任务执行列表
     */
    @RequestMapping("/list")
    public DataGrid<SchedulerTaskRecordVO> listRecord(@RequestBody SchedulerTaskRecordRequest request) {
        return schedulerTaskRecordService.listTaskRecord(request);
    }

    /**
     * 查询调度任务执行详情
     */
    @RequestMapping("/detail")
    public SchedulerTaskRecordVO getRecordDetail(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskRecordService.getDetailById(id);
    }

    /**
     * 查询调度任务机器
     */
    @RequestMapping("/machines")
    public List<SchedulerTaskMachineRecordVO> listMachinesRecord(@RequestBody SchedulerTaskRecordRequest request) {
        Long recordId = Valid.notNull(request.getRecordId());
        return schedulerTaskRecordService.listMachinesRecord(recordId);
    }

    /**
     * 查询调度任务状态
     */
    @RequestMapping("/list-status")
    public List<SchedulerTaskRecordStatusVO> listRecordStatus(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.listRecordStatus(idList, request.getMachineRecordIdList());
    }

    /**
     * 查询调度任务机器状态
     */
    @RequestMapping("/machines-status")
    public List<SchedulerTaskMachineRecordStatusVO> listMachineRecordStatus(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.listMachineRecordStatus(idList);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_TASK_RECORD)
    public Integer deleteRecord(@RequestBody SchedulerTaskRecordRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return schedulerTaskRecordService.deleteTaskRecord(idList);
    }

    /**
     * 停止所有
     */
    @RequestMapping("/terminated-all")
    @EventLog(EventType.TERMINATED_ALL_SCHEDULER_TASK)
    public HttpWrapper<?> terminatedAll(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        schedulerTaskRecordService.terminatedAll(id);
        return HttpWrapper.ok();
    }

    /**
     * 停止单个
     */
    @RequestMapping("/terminated-machine")
    @EventLog(EventType.TERMINATED_SCHEDULER_TASK_MACHINE)
    public HttpWrapper<?> terminatedMachine(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        Long machineRecordId = Valid.notNull(request.getMachineRecordId());
        schedulerTaskRecordService.terminatedMachine(id, machineRecordId);
        return HttpWrapper.ok();
    }

    /**
     * 跳过单个
     */
    @RequestMapping("/skip-machine")
    @EventLog(EventType.SKIP_SCHEDULER_TASK_MACHINE)
    public HttpWrapper<?> skipMachine(@RequestBody SchedulerTaskRecordRequest request) {
        Long id = Valid.notNull(request.getId());
        Long machineRecordId = Valid.notNull(request.getMachineRecordId());
        schedulerTaskRecordService.skipMachine(id, machineRecordId);
        return HttpWrapper.ok();
    }

}
