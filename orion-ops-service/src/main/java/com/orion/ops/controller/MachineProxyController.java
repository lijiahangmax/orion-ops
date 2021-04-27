package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.ProxyType;
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
        request.setId(null);
        this.check(request);
        return machineProxyService.addUpdateProxy(request);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Integer update(@RequestBody MachineProxyRequest request) {
        Valid.notNull(request.getId());
        this.check(request);
        return machineProxyService.addUpdateProxy(request).intValue();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineProxyVO> list(@RequestBody MachineProxyRequest request) {
        return machineProxyService.listProxy(request);
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
     * 合法校验
     */
    private void check(MachineProxyRequest request) {
        String host = Valid.notBlank(request.getHost());
        Valid.notNull(request.getPort());
        Valid.notNull(ProxyType.of(request.getType()));
        if (!Strings.isBlank(request.getUsername())) {
            Valid.notNull(request.getPassword());
        }
        if (!Matches.isIpv4(host)) {
            throw Exceptions.invalidArgument(Const.INVALID_PARAM);
        }
    }

    // @RequestMapping("/use/machine")

}
