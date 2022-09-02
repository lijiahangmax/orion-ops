package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.vo.machine.MachineEnvVO;

/**
 * 机器环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:12
 */
public class MachineEnvConversion {

    static {
        TypeStore.STORE.register(MachineEnvDO.class, MachineEnvVO.class, p -> {
            MachineEnvVO vo = new MachineEnvVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = MachineEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
