package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 14:06
 */
@Data
public class MachineEnvVO {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

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
            return vo;
        });
    }

}
