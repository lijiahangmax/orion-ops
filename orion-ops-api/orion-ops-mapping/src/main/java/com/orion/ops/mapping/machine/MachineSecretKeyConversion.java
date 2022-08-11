package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.vo.machine.MachineSecretKeyVO;

/**
 * 机器秘钥 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:13
 */
public class MachineSecretKeyConversion {

    static {
        TypeStore.STORE.register(MachineSecretKeyDO.class, MachineSecretKeyVO.class, p -> {
            MachineSecretKeyVO vo = new MachineSecretKeyVO();
            vo.setId(p.getId());
            vo.setName(p.getKeyName());
            vo.setPath(p.getSecretKeyPath());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
