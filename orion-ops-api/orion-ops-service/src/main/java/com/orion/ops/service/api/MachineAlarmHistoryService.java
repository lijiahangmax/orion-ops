package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.machine.MachineAlarmHistoryRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmHistoryVO;

/**
 * 机器报警历史服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
public interface MachineAlarmHistoryService {

    /**
     * 获取机器报警历史
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineAlarmHistoryVO> getAlarmHistory(MachineAlarmHistoryRequest request);

}
