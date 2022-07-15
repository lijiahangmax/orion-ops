package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用环境变量响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:25
 */
@Data
@ApiModel(value = "应用环境变量响应")
public class ApplicationEnvVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    /**
     * @see com.orion.ops.consts.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.consts.Const#FORBID_DELETE_NOT
     */
    @ApiModelProperty(value = "是否禁止删除 1可以删除 2禁止删除")
    private Integer forbidDelete;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

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
