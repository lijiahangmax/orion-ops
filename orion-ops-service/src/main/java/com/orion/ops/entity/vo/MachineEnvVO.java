package com.orion.ops.entity.vo;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器环境变量响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 14:06
 */
@Data
@ApiModel(value = "机器环境变量响应")
public class MachineEnvVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * @see com.orion.ops.consts.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.consts.Const#FORBID_DELETE_NOT
     */
    @ApiModelProperty(value = "是否禁止删除 1可以删除 2禁止删除")
    private Integer forbidDelete;

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
