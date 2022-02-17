package com.orion.ops.entity.vo;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.entity.domain.SystemEnvDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:36
 */
@Data
public class SystemEnvVO {

    /**
     * id
     */
    private Long id;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否禁止删除 1可以删除 2禁止删除
     *
     * @see Const#FORBID_DELETE_CAN
     * @see Const#FORBID_DELETE_NOT
     */
    private Integer forbidDelete;

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
