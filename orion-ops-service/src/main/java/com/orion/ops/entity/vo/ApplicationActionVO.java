package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * app操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:23
 */
@Data
public class ApplicationActionVO {

    /**
     * id
     */
    private Long id;

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
