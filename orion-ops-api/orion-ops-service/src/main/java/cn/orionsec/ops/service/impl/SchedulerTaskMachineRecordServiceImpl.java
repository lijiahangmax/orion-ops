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
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.SchedulerTaskMachineRecordDAO;
import cn.orionsec.ops.entity.domain.SchedulerTaskMachineRecordDO;
import cn.orionsec.ops.service.api.SchedulerTaskMachineRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
