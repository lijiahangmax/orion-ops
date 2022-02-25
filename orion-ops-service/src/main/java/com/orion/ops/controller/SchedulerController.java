package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.SchedulerTaskRequest;
import com.orion.ops.entity.vo.CronNextVO;
import com.orion.ops.entity.vo.SchedulerTaskVO;
import com.orion.ops.service.api.SchedulerTaskService;
import com.orion.ops.utils.Valid;
import com.orion.utils.collect.Lists;
import com.orion.utils.time.cron.Cron;
import com.orion.utils.time.cron.CronSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 调度任务api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 10:41
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/scheduler")
public class SchedulerController {

    @Resource
    private SchedulerTaskService schedulerTaskService;

    /**
     * 获取 cron 下几次执行时间
     */
    @RequestMapping("/cron-next")
    public CronNextVO getCronNextTime(@RequestBody SchedulerTaskRequest request) {
        String cron = Valid.notBlank(request.getExpression()).trim();
        Integer times = Valid.gt(request.getTimes(), 0);
        CronNextVO next = new CronNextVO();
        try {
            next.setNext(CronSupport.getNextTime(Cron.of(cron), times));
            next.setValid(true);
        } catch (Exception e) {
            next.setNext(Lists.empty());
            next.setValid(false);
        }
        return next;
    }

    /**
     * 添加任务
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_SCHEDULER_TASK)
    public Long addTask(@RequestBody SchedulerTaskRequest request) {
        Valid.allNotBlank(request.getName(), request.getCommand(), request.getExpression());
        Cron.of(request.getExpression());
        Valid.notNull(request.getSerializeType());
        Valid.notEmpty(request.getMachineIdList());
        return schedulerTaskService.addTask(request);
    }

    /**
     * 修改任务
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_SCHEDULER_TASK)
    public Integer updateTask(@RequestBody SchedulerTaskRequest request) {
        Valid.notNull(request.getId());
        Valid.allNotBlank(request.getName(), request.getCommand(), request.getExpression());
        Cron.of(request.getExpression());
        Valid.notEmpty(request.getMachineIdList());
        return schedulerTaskService.updateTask(request);
    }

    /**
     * 任务详情
     */
    @RequestMapping("/get")
    public SchedulerTaskVO getTaskDetail(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskService.getTaskDetail(id);
    }

    /**
     * 任务列表
     */
    @RequestMapping("/list")
    public DataGrid<SchedulerTaskVO> getTaskList(@RequestBody SchedulerTaskRequest request) {
        return schedulerTaskService.getTaskList(request);
    }

    /**
     * 更新状态
     */
    @RequestMapping("/update-status")
    @EventLog(EventType.UPDATE_SCHEDULER_TASK_STATUS)
    public Integer updateTaskStatus(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer status = Valid.in(request.getEnableStatus(), Const.ENABLE, Const.DISABLE);
        return schedulerTaskService.updateTaskStatus(id, status);
    }

    /**
     * 删除任务
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_SCHEDULER_TASK)
    public Integer deleteTask(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskService.deleteTask(id);
    }

    /**
     * 手动触发任务
     */
    @RequestMapping("/manual-trigger")
    @EventLog(EventType.MANUAL_TRIGGER_SCHEDULER_TASK)
    public HttpWrapper<?> manualTriggerTask(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        schedulerTaskService.manualTriggerTask(id);
        return HttpWrapper.ok();
    }

}
