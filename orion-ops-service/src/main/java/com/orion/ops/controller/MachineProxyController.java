package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.machine.ProxyType;
import com.orion.ops.entity.request.MachineProxyRequest;
import com.orion.ops.entity.vo.MachineProxyVO;
import com.orion.ops.service.api.MachineProxyService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器代理 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:11
 */
@Api(tags = "机器代理")
@RestController
@RestWrapper
@RequestMapping("/orion/api/proxy")
public class MachineProxyController {

    @Resource
    private MachineProxyService machineProxyService;

    @PostMapping("/add")
    @ApiOperation(value = "添加机器代理")
    @EventLog(EventType.ADD_MACHINE_PROXY)
    public Long addProxy(@RequestBody MachineProxyRequest request) {
        request.setId(null);
        this.check(request);
        if (!Strings.isBlank(request.getUsername())) {
            Valid.notNull(request.getPassword());
        }
        return machineProxyService.addProxy(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改机器代理")
    @EventLog(EventType.UPDATE_MACHINE_PROXY)
    public Integer update(@RequestBody MachineProxyRequest request) {
        Valid.notNull(request.getId());
        this.check(request);
        return machineProxyService.updateProxy(request);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取机器代理列表")
    public DataGrid<MachineProxyVO> list(@RequestBody MachineProxyRequest request) {
        return machineProxyService.listProxy(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取机器代理详情")
    public MachineProxyVO detail(@RequestBody MachineProxyRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineProxyService.getProxyDetail(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除机器代理")
    @EventLog(EventType.DELETE_MACHINE_PROXY)
    public Integer delete(@RequestBody MachineProxyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineProxyService.deleteProxy(idList);
    }

    /**
     * 合法校验
     */
    private void check(MachineProxyRequest request) {
        Valid.notBlank(request.getHost());
        Valid.notNull(request.getPort());
        Valid.notNull(ProxyType.of(request.getType()));
    }

}
