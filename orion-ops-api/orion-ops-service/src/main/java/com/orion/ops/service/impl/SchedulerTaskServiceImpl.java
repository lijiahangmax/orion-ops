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
package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.CnConst;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.constant.scheduler.SchedulerTaskStatus;
import com.orion.ops.dao.SchedulerTaskDAO;
import com.orion.ops.dao.SchedulerTaskMachineDAO;
import com.orion.ops.entity.domain.SchedulerTaskDO;
import com.orion.ops.entity.domain.SchedulerTaskMachineDO;
import com.orion.ops.entity.request.scheduler.SchedulerTaskRequest;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskVO;
import com.orion.ops.service.api.SchedulerTaskMachineRecordService;
import com.orion.ops.service.api.SchedulerTaskMachineService;
import com.orion.ops.service.api.SchedulerTaskRecordService;
import com.orion.ops.service.api.SchedulerTaskService;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.task.impl.SchedulerTaskImpl;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 调度任务 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Service("schedulerTaskService")
public class SchedulerTaskServiceImpl implements SchedulerTaskService {

    @Resource
    private SchedulerTaskDAO schedulerTaskDAO;

    @Resource
    private SchedulerTaskMachineDAO schedulerTaskMachineDAO;

    @Resource
    private SchedulerTaskMachineService schedulerTaskMachineService;

    @Resource
    private SchedulerTaskRecordService schedulerTaskRecordService;

    @Resource
    private SchedulerTaskMachineRecordService schedulerTaskMachineRecordService;

    @Resource
    private TaskRegister taskRegister;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTask(SchedulerTaskRequest request) {
        String name = request.getName();
        this.checkNamePresent(null, name);
        // 设置任务表
        SchedulerTaskDO task = new SchedulerTaskDO();
        task.setTaskName(name);
        task.setDescription(request.getDescription());
        task.setTaskCommand(request.getCommand());
        task.setExpression(request.getExpression().trim());
        task.setEnableStatus(Const.DISABLE);
        task.setLatelyStatus(SchedulerTaskStatus.WAIT.getStatus());
        task.setSerializeType(request.getSerializeType());
        task.setExceptionHandler(request.getExceptionHandler());
        schedulerTaskDAO.insert(task);
        Long taskId = task.getId();
        // 设置机器表
        request.getMachineIdList().stream().map(s -> {
            SchedulerTaskMachineDO m = new SchedulerTaskMachineDO();
            m.setTaskId(taskId);
            m.setMachineId(s);
            return m;
        }).forEach(schedulerTaskMachineDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(task);
        return taskId;
    }

    @Override
    public Integer updateTask(SchedulerTaskRequest request) {
        Long id = request.getId();
        String name = request.getName();
        this.checkNamePresent(id, name);
        // 查询原始数据
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.UNKNOWN_DATA);
        // 更新
        SchedulerTaskDO update = new SchedulerTaskDO();
        update.setId(id);
        update.setTaskName(name);
        update.setExpression(request.getExpression().trim());
        update.setTaskCommand(request.getCommand());
        update.setDescription(request.getDescription());
        update.setEnableStatus(request.getEnableStatus());
        update.setEnableStatus(Const.DISABLE);
        update.setLatelyStatus(SchedulerTaskStatus.WAIT.getStatus());
        update.setSerializeType(request.getSerializeType());
        update.setExceptionHandler(request.getExceptionHandler());
        update.setUpdateTime(new Date());
        int effect = schedulerTaskDAO.updateById(update);
        // 查询机器信息
        List<SchedulerTaskMachineDO> machines = schedulerTaskMachineService.selectByTaskId(id);
        Map<Long, Long> beforeMachineMap = machines.stream().collect(Collectors.toMap(
                SchedulerTaskMachineDO::getMachineId, SchedulerTaskMachineDO::getId, (s1, s2) -> s1));
        List<Long> machineIdList = request.getMachineIdList();
        // 添加增量的机器
        machineIdList.stream()
                .filter(s -> !beforeMachineMap.containsKey(s))
                .map(s -> {
                    SchedulerTaskMachineDO m = new SchedulerTaskMachineDO();
                    m.setTaskId(id);
                    m.setMachineId(s);
                    return m;
                }).forEach(schedulerTaskMachineDAO::insert);
        // 包含则移除
        machineIdList.stream().filter(beforeMachineMap::containsKey).forEach(beforeMachineMap::remove);
        // 移除不存在的机器
        Collection<Long> values = beforeMachineMap.values();
        if (!values.isEmpty()) {
            effect += schedulerTaskMachineDAO.deleteBatchIds(values);
        }
        // 停止任务
        taskRegister.cancel(TaskType.SCHEDULER_TASK, id);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, task.getTaskName());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID_LIST, request.getMachineIdList());
        return effect;
    }

    @Override
    public SchedulerTaskVO getTaskDetail(Long id) {
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.UNKNOWN_DATA);
        SchedulerTaskVO vo = Converts.to(task, SchedulerTaskVO.class);
        // 查询机器
        List<SchedulerTaskMachineDO> machines = schedulerTaskMachineService.selectByTaskId(id);
        List<Long> machineIdList = machines.stream()
                .map(SchedulerTaskMachineDO::getMachineId)
                .collect(Collectors.toList());
        vo.setMachineIdList(machineIdList);
        return vo;
    }

    @Override
    public DataGrid<SchedulerTaskVO> getTaskList(SchedulerTaskRequest request) {
        LambdaQueryWrapper<SchedulerTaskDO> wrapper = new LambdaQueryWrapper<SchedulerTaskDO>()
                .like(Strings.isNotBlank(request.getName()), SchedulerTaskDO::getTaskName, request.getName())
                .like(Objects.nonNull(request.getDescription()), SchedulerTaskDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getEnableStatus()), SchedulerTaskDO::getEnableStatus, request.getEnableStatus())
                .eq(Objects.nonNull(request.getLatelyStatus()), SchedulerTaskDO::getLatelyStatus, request.getLatelyStatus());
        return DataQuery.of(schedulerTaskDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(SchedulerTaskVO.class);
    }

    @Override
    public Integer updateTaskStatus(Long id, Integer status) {
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.UNKNOWN_DATA);
        SchedulerTaskDO update = new SchedulerTaskDO();
        update.setId(id);
        update.setEnableStatus(status);
        update.setLatelyStatus(null);
        int effect = schedulerTaskDAO.updateById(update);
        boolean enable = Const.ENABLE.equals(status);
        // 停止任务
        taskRegister.cancel(TaskType.SCHEDULER_TASK, id);
        if (enable) {
            // 启动
            taskRegister.submit(TaskType.SCHEDULER_TASK, task.getExpression(), id);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.OPERATOR, enable ? CnConst.ENABLE : CnConst.DISABLE);
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, task.getTaskName());
        return effect;
    }

    @Override
    public Integer deleteTask(Long id) {
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.UNKNOWN_DATA);
        // 停止任务
        taskRegister.cancel(TaskType.SCHEDULER_TASK, id);
        // 删除任务
        int effect = schedulerTaskDAO.deleteById(id);
        // 删除任务机器
        effect += schedulerTaskMachineService.deleteByTaskId(id);
        // 删除任务明细
        // effect += schedulerTaskRecordService.deleteByTaskId(id);
        // 删除任务明细
        // effect += schedulerTaskMachineRecordService.deleteByTaskId(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, task.getTaskName());
        return effect;
    }

    @Override
    public void manualTriggerTask(Long id) {
        SchedulerTaskDO task = schedulerTaskDAO.selectById(id);
        Valid.notNull(task, MessageConst.UNKNOWN_DATA);
        Valid.isTrue(Const.ENABLE.equals(task.getEnableStatus()), MessageConst.TASK_NOT_ENABLED);
        // 手动执行
        new SchedulerTaskImpl(id).run();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, task.getTaskName());
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<SchedulerTaskDO> presentWrapper = new LambdaQueryWrapper<SchedulerTaskDO>()
                .ne(id != null, SchedulerTaskDO::getId, id)
                .eq(SchedulerTaskDO::getTaskName, name);
        boolean present = DataQuery.of(schedulerTaskDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
