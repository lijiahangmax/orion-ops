package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.MachineRoomRequest;
import com.orion.ops.entity.vo.MachineRoomVO;
import com.orion.ops.service.api.MachineRoomService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 机房
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 20:14
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-room")
public class MachineRoomController {

    @Resource
    private MachineRoomService machineRoomService;

    /**
     * 添加
     */
    @RequestMapping("/add")
    public Long add(@RequestBody MachineRoomRequest request) {
        request.setId(null);
        Valid.notBlank(request.getName());
        return machineRoomService.addUpdateMachineRoom(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Integer update(@RequestBody MachineRoomRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getName());
        return machineRoomService.addUpdateMachineRoom(request).intValue();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineRoomVO> list(@RequestBody MachineRoomRequest request) {
        return machineRoomService.listMachineRoom(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer delete(@RequestBody MachineRoomRequest request) {
        Valid.notNull(request.getId());
        return machineRoomService.deleteMachineRoom(request);
    }

    // @RequestMapping("/use/machine")

}
