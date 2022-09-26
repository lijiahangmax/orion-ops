package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineGroupDO;
import com.orion.ops.entity.vo.machine.MachineGroupTreeVO;

/**
 * 机器分组 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 17:39
 */
public class MachineGroupConversion {

    static {
        TypeStore.STORE.register(MachineGroupDO.class, MachineGroupTreeVO.class, p -> {
            MachineGroupTreeVO vo = new MachineGroupTreeVO();
            vo.setId(p.getId());
            vo.setParentId(p.getParentId());
            vo.setName(p.getGroupName());
            vo.setSort(p.getSort());
            return vo;
        });
    }

}
