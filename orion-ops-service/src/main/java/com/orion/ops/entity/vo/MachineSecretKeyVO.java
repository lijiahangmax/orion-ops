package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 秘钥vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 13:44
 */
@Data
public class MachineSecretKeyVO {

    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * 路径
     */
    private String path;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

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
