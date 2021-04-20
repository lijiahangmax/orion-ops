package com.orion.ops.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 10:15
 */
@AllArgsConstructor
@Getter
public enum RoleType {

    /**
     * 管理员
     */
    ADMINISTRATOR(1),

    /**
     * 开发
     */
    DEVELOPER(2),

    /**
     * 运维
     */
    OPERATION(3),

    ;

    Integer type;

}
