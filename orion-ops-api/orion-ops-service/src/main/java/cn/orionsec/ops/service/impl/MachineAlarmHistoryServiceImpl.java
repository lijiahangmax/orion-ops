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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.ops.dao.MachineAlarmHistoryDAO;
import cn.orionsec.ops.entity.domain.MachineAlarmHistoryDO;
import cn.orionsec.ops.entity.request.machine.MachineAlarmHistoryRequest;
import cn.orionsec.ops.entity.vo.machine.MachineAlarmHistoryVO;
import cn.orionsec.ops.service.api.MachineAlarmHistoryService;
import cn.orionsec.ops.utils.DataQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 机器报警历史服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
@Service("machineAlarmHistoryService")
public class MachineAlarmHistoryServiceImpl implements MachineAlarmHistoryService {

    // TODO 最近7天的报警记录

    @Resource
    private MachineAlarmHistoryDAO machineAlarmHistoryDAO;

    @Override
    public DataGrid<MachineAlarmHistoryVO> getAlarmHistory(MachineAlarmHistoryRequest request) {
        LambdaQueryWrapper<MachineAlarmHistoryDO> wrapper = new LambdaQueryWrapper<MachineAlarmHistoryDO>()
                .eq(MachineAlarmHistoryDO::getMachineId, request.getMachineId())
                .eq(Objects.nonNull(request.getType()), MachineAlarmHistoryDO::getAlarmType, request.getType())
                .between(Objects1.isNoneNull(request.getAlarmValueStart(), request.getAlarmValueEnd()),
                        MachineAlarmHistoryDO::getAlarmValue, request.getAlarmValueStart(), request.getAlarmValueEnd())
                .between(Objects1.isNoneNull(request.getAlarmTimeStart(), request.getAlarmTimeEnd()),
                        MachineAlarmHistoryDO::getAlarmTime, request.getAlarmTimeStart(), request.getAlarmTimeEnd())
                .orderByDesc(MachineAlarmHistoryDO::getAlarmTime);
        return DataQuery.of(machineAlarmHistoryDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineAlarmHistoryVO.class);
    }

}
