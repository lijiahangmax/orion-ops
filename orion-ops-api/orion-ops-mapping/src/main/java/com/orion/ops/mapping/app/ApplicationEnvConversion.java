package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.app.ApplicationEnvAttr;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.ops.entity.vo.app.ApplicationEnvVO;

/**
 * 应用环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:40
 */
public class ApplicationEnvConversion {

    static {
        TypeStore.STORE.register(ApplicationEnvDO.class, ApplicationEnvVO.class, p -> {
            ApplicationEnvVO vo = new ApplicationEnvVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setProfileId(p.getProfileId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = ApplicationEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
