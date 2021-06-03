package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.AuthType;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SystemType;
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
    public Long add(@RequestBody MachineInfoRequest request) {
        request.setId(null);
        this.check(request);
        return machineInfoService.addUpdateMachine(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public int update(@RequestBody MachineInfoRequest request) {
        Valid.notNull(request.getId());
        this.check(request);
        return machineInfoService.addUpdateMachine(request).intValue();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer delete(@RequestBody MachineInfoRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineInfoService.deleteMachine(idList);
    }

    /**
     * 停用/启用
     */
    @RequestMapping("/status")
    public Integer status(@RequestBody MachineInfoRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        Integer status = Valid.notNull(request.getStatus());
        Valid.isTrue(Const.ENABLE.equals(status) || Const.DISABLE.equals(status));
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
    public Long copy(@RequestBody MachineInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineInfoService.copyMachine(id);
    }

    /**
     * 同步属性
     */
    @RequestMapping("/syncProp")
    public String syncProperties(@RequestBody MachineInfoRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getSyncProp());
        return machineInfoService.syncProperties(request);
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

    // 批量删除
    // 批量停用
    // run tpl
    // 配置模板

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
        Integer authType = Valid.notNull(request.getAuthType());
        AuthType authTypeEnum = Valid.notNull(AuthType.of(authType));
        if (AuthType.PASSWORD.equals(authTypeEnum)) {
            Valid.notBlank(request.getPassword());
        }
        Integer systemType = Valid.notNull(request.getSystemType());
        Valid.notNull(SystemType.of(systemType));
    }

}
