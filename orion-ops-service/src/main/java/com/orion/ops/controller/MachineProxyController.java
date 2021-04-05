package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.PageRequest;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.request.MachineProxyRequest;
import com.orion.ops.entity.vo.MachineProxyVO;
import com.orion.ops.service.api.MachineProxyService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Matches;
import com.orion.utils.Strings;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 机器代理
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:11
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/proxy")
public class MachineProxyController {

    @Resource
    private MachineProxyService machineProxyService;

    /**
     * 添加
     */
    @RequestMapping("/add")
    public Long addProxy(@RequestBody MachineProxyRequest request) {
        this.check(request);
        MachineProxyDO proxy = new MachineProxyDO();
        proxy.setProxyHost(request.getHost());
        proxy.setProxyPort(request.getPort());
        proxy.setProxyUsername(request.getUsername());
        proxy.setProxyPassword(request.getPassword());
        proxy.setDescription(request.getDescription());
        return machineProxyService.addProxy(proxy);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineProxyVO> list(@RequestBody PageRequest request) {
        return machineProxyService.listProxy(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Integer update(@RequestBody MachineProxyRequest request) {
        this.check(request);
        Valid.notNull(request.getId());
        MachineProxyDO proxy = new MachineProxyDO();
        proxy.setId(request.getId());
        proxy.setProxyHost(request.getHost());
        proxy.setProxyPort(request.getPort());
        proxy.setProxyUsername(request.getUsername());
        proxy.setProxyPassword(request.getPassword());
        proxy.setDescription(request.getDescription());
        return machineProxyService.updateProxy(proxy);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer delete(@RequestBody MachineProxyRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineProxyService.deleteProxy(id);
    }

    /**
     * 检查
     */
    private void check(MachineProxyRequest request) {
        String host = Valid.notBlank(request.getHost());
        Valid.notNull(request.getPort());
        if (!Strings.isBlank(request.getUsername())) {
            Valid.notNull(request.getPassword());
        }
        if (!Matches.isIpv4(host)) {
            throw Exceptions.invalidArgument(Const.INVALID_PARAM);
        }
    }

}
