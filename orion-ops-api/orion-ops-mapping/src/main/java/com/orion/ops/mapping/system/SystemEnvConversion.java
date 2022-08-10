package com.orion.ops.mapping.system;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.domain.SystemEnvDO;
import com.orion.ops.entity.vo.system.SystemEnvVO;

/**
 * 系统环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:23
 */
public class SystemEnvConversion {

    static {
        TypeStore.STORE.register(SystemEnvDO.class, SystemEnvVO.class, p -> {
            SystemEnvVO vo = new SystemEnvVO();
            vo.setId(p.getId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = SystemEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
