package com.orion.ops.utils;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.RoleType;
import com.orion.ops.consts.UserHolder;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.servlet.web.CookiesExt;
import com.orion.utils.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 * 环境工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 9:52
 */
public class Currents {

    private Currents() {
    }

    /**
     * 获取当前登录token
     *
     * @param request request
     * @return token
     */
    public static String getLoginToken(HttpServletRequest request) {
        return getToken(request, Const.LOGIN_TOKEN);
    }

    /**
     * 获取token
     *
     * @param request request
     * @param token   tokenKey
     * @return token
     */
    public static String getToken(HttpServletRequest request, String token) {
        String t = request.getHeader(token);
        if (Strings.isEmpty(t)) {
            t = CookiesExt.get(request, token);
        }
        return t;
    }

    /**
     * 获取当前登录用户
     *
     * @return 用户
     */
    public static UserDTO getUser() {
        return UserHolder.get();
    }

    /**
     * 获取当前登录用户id
     *
     * @return id
     */
    public static Long getUserId() {
        return UserHolder.get().getId();
    }

    /**
     * 是否为 超级管理员
     *
     * @return true 超级管理员
     */
    public static boolean isSuperAdministrator() {
        UserDTO user = UserHolder.get();
        if (user == null) {
            return false;
        }
        Integer roleType = user.getRoleType();
        return RoleType.isSuperAdministrator(roleType);
    }

    /**
     * 是否为 超级管理员/管理员
     *
     * @return true 超级管理员/管理员
     */
    public static boolean isAdministrator() {
        UserDTO user = UserHolder.get();
        if (user == null) {
            return false;
        }
        Integer roleType = user.getRoleType();
        return RoleType.isAdministrator(roleType);
    }

}
