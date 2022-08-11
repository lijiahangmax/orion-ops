package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.vo.machine.MachineProxyVO;

/**
 * 机器代理 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:10
 */
public class MachineProxyConversion {

    static {
        TypeStore.STORE.register(MachineProxyDO.class, MachineProxyVO.class, p -> {
            MachineProxyVO vo = new MachineProxyVO();
            vo.setId(p.getId());
            vo.setHost(p.getProxyHost());
            vo.setPort(p.getProxyPort());
            vo.setUsername(p.getProxyUsername());
            vo.setType(p.getProxyType());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
