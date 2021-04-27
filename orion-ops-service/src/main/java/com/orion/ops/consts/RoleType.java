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
     * 超级管理员
     */
    SUPER_ADMINISTRATOR(10),

    /**
     * 管理员
     */
    ADMINISTRATOR(20),

    /**
     * 开发
     */
    DEVELOPER(30),

    /**
     * 运维
     */
    OPERATION(40),

    ;

    Integer type;

    public static RoleType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RoleType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 是否为 超级管理员
     *
     * @return true 超级管理员
     */
    public static boolean isSuperAdministrator(Integer type) {
        RoleType role = of(type);
        if (role == null) {
            return false;
        }
        return SUPER_ADMINISTRATOR.equals(role);
    }

    /**
     * 是否为 超级管理员/管理员
     *
     * @return true 超级管理员/管理员
     */
    public static boolean isAdministrator(Integer type) {
        RoleType role = of(type);
        if (role == null) {
            return false;
        }
        return SUPER_ADMINISTRATOR.equals(role) || ADMINISTRATOR.equals(role);
    }

}
