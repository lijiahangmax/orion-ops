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

import cn.orionsec.ops.dao.MachineAlarmGroupDAO;
import cn.orionsec.ops.entity.domain.MachineAlarmGroupDO;
import cn.orionsec.ops.service.api.MachineAlarmGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
@Service("machineAlarmGroupService")
public class MachineAlarmGroupServiceImpl implements MachineAlarmGroupService {

    @Resource
    private MachineAlarmGroupDAO machineAlarmGroupDAO;

    @Override
    public List<MachineAlarmGroupDO> selectByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getMachineId, machineId);
        return machineAlarmGroupDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getMachineId, machineId);
        return machineAlarmGroupDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .in(MachineAlarmGroupDO::getMachineId, machineIdList);
        return machineAlarmGroupDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByGroupId(Long groupId) {
        LambdaQueryWrapper<MachineAlarmGroupDO> wrapper = new LambdaQueryWrapper<MachineAlarmGroupDO>()
                .eq(MachineAlarmGroupDO::getGroupId, groupId);
        return machineAlarmGroupDAO.delete(wrapper);
    }

}
