package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Objects1;
import com.orion.ops.dao.MachineAlarmHistoryDAO;
import com.orion.ops.entity.domain.MachineAlarmHistoryDO;
import com.orion.ops.entity.request.machine.MachineAlarmHistoryRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmHistoryVO;
import com.orion.ops.service.api.MachineAlarmHistoryService;
import com.orion.ops.utils.DataQuery;
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
