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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.time.cron.Cron;
import cn.orionsec.kit.lang.utils.time.cron.CronSupport;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.scheduler.SchedulerTaskRequest;
import cn.orionsec.ops.entity.vo.scheduler.CronNextVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskVO;
import cn.orionsec.ops.service.api.SchedulerTaskService;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 调度任务 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 10:41
 */
@Api(tags = "调度任务")
@RestController
@RestWrapper
@RequestMapping("/orion/api/scheduler")
public class SchedulerController {

    @Resource
    private SchedulerTaskService schedulerTaskService;

    @PostMapping("/cron-next")
    @ApiOperation(value = "获取cron下几次执行时间")
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

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加调度任务")
    @EventLog(EventType.ADD_SCHEDULER_TASK)
    public Long addTask(@RequestBody SchedulerTaskRequest request) {
        Valid.allNotBlank(request.getName(), request.getCommand(), request.getExpression());
        Cron.of(request.getExpression());
        Valid.notNull(request.getSerializeType());
        Valid.notEmpty(request.getMachineIdList());
        return schedulerTaskService.addTask(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改调度任务")
    @EventLog(EventType.UPDATE_SCHEDULER_TASK)
    public Integer updateTask(@RequestBody SchedulerTaskRequest request) {
        Valid.notNull(request.getId());
        Valid.allNotBlank(request.getName(), request.getCommand(), request.getExpression());
        Cron.of(request.getExpression());
        Valid.notEmpty(request.getMachineIdList());
        return schedulerTaskService.updateTask(request);
    }

    @PostMapping("/get")
    @ApiOperation(value = "获取调度任务详情")
    public SchedulerTaskVO getTaskDetail(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskService.getTaskDetail(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取调度任务列表")
    public DataGrid<SchedulerTaskVO> getTaskList(@RequestBody SchedulerTaskRequest request) {
        return schedulerTaskService.getTaskList(request);
    }

    @DemoDisableApi
    @PostMapping("/update-status")
    @ApiOperation(value = "更新调度任务状态")
    @EventLog(EventType.UPDATE_SCHEDULER_TASK_STATUS)
    public Integer updateTaskStatus(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer status = Valid.in(request.getEnableStatus(), Const.ENABLE, Const.DISABLE);
        return schedulerTaskService.updateTaskStatus(id, status);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除调度任务")
    @EventLog(EventType.DELETE_SCHEDULER_TASK)
    public Integer deleteTask(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        return schedulerTaskService.deleteTask(id);
    }

    @PostMapping("/manual-trigger")
    @ApiOperation(value = "手动触发调度任务")
    @EventLog(EventType.MANUAL_TRIGGER_SCHEDULER_TASK)
    public HttpWrapper<?> manualTriggerTask(@RequestBody SchedulerTaskRequest request) {
        Long id = Valid.notNull(request.getId());
        schedulerTaskService.manualTriggerTask(id);
        return HttpWrapper.ok();
    }

}
