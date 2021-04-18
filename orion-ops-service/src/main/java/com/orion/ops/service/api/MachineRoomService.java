package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.MachineRoomRequest;
import com.orion.ops.entity.vo.MachineRoomVO;

/**
 * 机房
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 20:15
 */
public interface MachineRoomService {

    /**
     * 添加/修改 机房信息
     *
     * @param request request
     * @return id/effect
     */
    Long addUpdateMachineRoom(MachineRoomRequest request);

    /**
     * 查询列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineRoomVO> listMachineRoom(MachineRoomRequest request);

    /**
     * 删除机房信息
     *
     * @param request request
     * @return effect
     */
    Integer deleteMachineRoom(MachineRoomRequest request);

}
