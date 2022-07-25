package com.orion.ops.constant.user;

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
    ADMINISTRATOR(10, "/menu/menu-admin.json"),

    /**
     * 开发
     */
    DEVELOPER(20, "/menu/menu-dev.json"),

    /**
     * 运维
     */
    OPERATION(30, "/menu/menu-opt.json"),

    ;

    private final Integer type;

    private final String menuPath;

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
     * 是否为 管理员
     *
     * @return true 管理员
     */
    public static boolean isAdministrator(Integer type) {
        RoleType role = of(type);
        if (role == null) {
            return false;
        }
        return ADMINISTRATOR.equals(role);
    }

}
