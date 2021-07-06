package com.orion.ops.entity.vo;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:25
 */
@Data
public class ApplicationEnvVO {

    /**
     * id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * 是否禁止删除 1可以删除 2禁止删除
     *
     * @see com.orion.ops.consts.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.consts.Const#FORBID_DELETE_NOT
     */
    private Integer forbidDelete;

    static {
        TypeStore.STORE.register(ApplicationEnvDO.class, ApplicationEnvVO.class, p -> {
            ApplicationEnvVO vo = new ApplicationEnvVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setProfileId(p.getProfileId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            Integer forbidDelete = ApplicationEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
