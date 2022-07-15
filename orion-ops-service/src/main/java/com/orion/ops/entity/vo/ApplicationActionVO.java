package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationActionDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用操作响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:23
 */
@Data
@ApiModel(value = "应用操作响应")
public class ApplicationActionVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @see com.orion.ops.consts.app.ActionType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "命令")
    private String command;

    static {
        TypeStore.STORE.register(ApplicationActionDO.class, ApplicationActionVO.class, p -> {
            ApplicationActionVO vo = new ApplicationActionVO();
            vo.setId(p.getId());
            vo.setName(p.getActionName());
            vo.setType(p.getActionType());
            vo.setCommand(p.getActionCommand());
            return vo;
        });
    }

}
