package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.SchedulerTaskMachineDAO;
import com.orion.ops.entity.domain.SchedulerTaskMachineDO;
import com.orion.ops.service.api.SchedulerTaskMachineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 调度任务机器 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Service("schedulerTaskMachineService")
public class SchedulerTaskMachineServiceImpl implements SchedulerTaskMachineService {

    @Resource
    private SchedulerTaskMachineDAO schedulerTaskMachineDAO;

    @Override
    public List<SchedulerTaskMachineDO> selectByTaskId(Long taskId) {
        LambdaQueryWrapper<SchedulerTaskMachineDO> wrapper = new LambdaQueryWrapper<SchedulerTaskMachineDO>()
                .eq(SchedulerTaskMachineDO::getTaskId, taskId);
        return schedulerTaskMachineDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<SchedulerTaskMachineDO> wrapper = new LambdaQueryWrapper<SchedulerTaskMachineDO>()
                .eq(SchedulerTaskMachineDO::getTaskId, taskId);
        return schedulerTaskMachineDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByMachineId(Long machineId) {
        LambdaQueryWrapper<SchedulerTaskMachineDO> wrapper = new LambdaQueryWrapper<SchedulerTaskMachineDO>()
                .eq(SchedulerTaskMachineDO::getMachineId, machineId);
        return schedulerTaskMachineDAO.delete(wrapper);
    }

}
