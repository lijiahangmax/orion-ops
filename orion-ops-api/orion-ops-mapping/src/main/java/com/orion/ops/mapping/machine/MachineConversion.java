package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.request.machine.MachineInfoRequest;
import com.orion.ops.entity.vo.machine.MachineInfoVO;

/**
 * 机器信息 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:10
 */
public class MachineConversion {

    static {
        TypeStore.STORE.register(MachineInfoDO.class, MachineInfoVO.class, p -> {
            MachineInfoVO vo = new MachineInfoVO();
            vo.setId(p.getId());
            vo.setProxyId(p.getProxyId());
            vo.setKeyId(p.getKeyId());
            vo.setHost(p.getMachineHost());
            vo.setSshPort(p.getSshPort());
            vo.setName(p.getMachineName());
            vo.setTag(p.getMachineTag());
            vo.setDescription(p.getDescription());
            vo.setUsername(p.getUsername());
            vo.setAuthType(p.getAuthType());
            vo.setStatus(p.getMachineStatus());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoRequest.class, MachineInfoDO.class, p -> {
            MachineInfoDO d = new MachineInfoDO();
            d.setId(p.getId());
            d.setProxyId(p.getProxyId());
            d.setKeyId(p.getKeyId());
            d.setMachineHost(p.getHost());
            d.setSshPort(p.getSshPort());
            d.setMachineName(p.getName());
            d.setMachineTag(p.getTag());
            d.setDescription(p.getDescription());
            d.setUsername(p.getUsername());
            d.setAuthType(p.getAuthType());
            d.setMachineStatus(p.getStatus());
            return d;
        });
    }

}
