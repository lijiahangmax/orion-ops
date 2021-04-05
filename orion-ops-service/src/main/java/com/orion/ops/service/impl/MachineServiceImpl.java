package com.orion.ops.service.impl;

import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.service.api.MachineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 机器管理service
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
@Service("machineService")
public class MachineServiceImpl implements MachineService {

    @Resource
    private MachineProxyDAO machineProxyDAO;

    public Long addProxy(MachineProxyDO proxy) {
        machineProxyDAO.insert(proxy);
        return proxy.getId();
    }

}
