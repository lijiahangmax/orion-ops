package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationDeployActionDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * app部署操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:23
 */
@Data
public class ApplicationDeployActionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 步骤
     */
    private Integer step;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     *
     * @see com.orion.ops.consts.app.ActionType
     */
    private Integer type;

    /**
     * 命令
     */
    private String command;

    static {
        TypeStore.STORE.register(ApplicationDeployActionDO.class, ApplicationDeployActionVO.class, p -> {
            ApplicationDeployActionVO vo = new ApplicationDeployActionVO();
            vo.setId(p.getId());
            vo.setName(p.getActionName());
            vo.setType(p.getActionType());
            vo.setCommand(p.getActionCommand());
            return vo;
        });
    }

}
