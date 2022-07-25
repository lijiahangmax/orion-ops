package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.SchedulerTaskMachineRecordDAO;
import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.service.api.SchedulerTaskMachineRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 调度任务执行明细机器详情 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
@Service("schedulerTaskMachineRecordService")
public class SchedulerTaskMachineRecordServiceImpl implements SchedulerTaskMachineRecordService {

    @Resource
    private SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO;

    @Override
    public Integer deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<SchedulerTaskMachineRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskMachineRecordDO>()
                .eq(SchedulerTaskMachineRecordDO::getTaskId, taskId);
        return schedulerTaskMachineRecordDAO.delete(wrapper);
    }

    @Override
    public List<SchedulerTaskMachineRecordDO> selectByRecordId(Long recordId) {
        LambdaQueryWrapper<SchedulerTaskMachineRecordDO> wrapper = new LambdaQueryWrapper<SchedulerTaskMachineRecordDO>()
                .eq(SchedulerTaskMachineRecordDO::getRecordId, recordId);
        return schedulerTaskMachineRecordDAO.selectList(wrapper);
    }

    @Override
    public String getTaskMachineLogPath(Long id) {
        return Optional.ofNullable(schedulerTaskMachineRecordDAO.selectById(id))
                .map(SchedulerTaskMachineRecordDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

}
