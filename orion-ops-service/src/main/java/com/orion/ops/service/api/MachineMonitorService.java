package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.MachineMonitorRequest;
import com.orion.ops.entity.vo.MachineMonitorVO;

import java.util.List;

/**
 * 机器监控服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:46
 */
public interface MachineMonitorService {

    /**
     * 获取监控列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineMonitorVO> getMonitorList(MachineMonitorRequest request);

    /**
     * 通过机器 id 删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

}
