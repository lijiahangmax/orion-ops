package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.machine.MachineAuthType;
import com.orion.ops.entity.request.MachineInfoRequest;
import com.orion.ops.entity.vo.MachineInfoVO;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:11
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine")
public class MachineInfoController {

    @Resource
    private MachineInfoService machineInfoService;

    /**
     * 添加
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_MACHINE)
    public Long add(@RequestBody MachineInfoRequest request) {
        this.check(request);
        MachineAuthType machineAuthTypeEnum = Valid.notNull(MachineAuthType.of(request.getAuthType()));
        if (MachineAuthType.PASSWORD.equals(machineAuthTypeEnum)) {
            Valid.notBlank(request.getPassword());
        }
        return machineInfoService.addMachine(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_MACHINE)
    public int update(@RequestBody MachineInfoRequest request) {
        Valid.notNull(request.getId());
        this.check(request);
        return machineInfoService.updateMachine(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_MACHINE)
    public Integer delete(@RequestBody MachineInfoRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        // 设置日志参数
        return machineInfoService.deleteMachine(idList);
    }

    /**
     * 停用/启用
     */
    @RequestMapping("/status")
    @EventLog(EventType.CHANGE_MACHINE_STATUS)
    public Integer status(@RequestBody MachineInfoRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        Integer status = Valid.notNull(request.getStatus());
        Valid.in(status, Const.ENABLE, Const.DISABLE);
        return machineInfoService.updateStatus(idList, status);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineInfoVO> list(@RequestBody MachineInfoRequest request) {
        return machineInfoService.listMachine(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public MachineInfoVO detail(@RequestBody MachineInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineInfoService.machineDetail(id);
    }

    /**
     * 复制
     */
    @RequestMapping("/copy")
    @EventLog(EventType.COPY_MACHINE)
    public Long copy(@RequestBody MachineInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineInfoService.copyMachine(id);
    }

    /**
     * 尝试 ping 主机
     */
    @RequestMapping("/test/ping")
    public Integer ping(@RequestBody MachineInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineInfoService.testPing(id);
    }

    /**
     * 尝试 连接 主机
     */
    @RequestMapping("/test/connect")
    public Integer connect(@RequestBody MachineInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineInfoService.testConnect(id);
    }

    /**
     * 合法校验
     */
    private void check(MachineInfoRequest request) {
        Valid.notBlank(request.getHost());
        Integer sshPort = Valid.notNull(request.getSshPort());
        Valid.gt(sshPort, 0, "ssh端口不正确");
        Valid.lte(sshPort, 65535, "ssh端口不正确");
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getUsername());
    }

}
