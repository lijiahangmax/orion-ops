package com.orion.ops.utils;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.consts.user.UserHolder;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.utils.Exceptions;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
        return request.getHeader(token);
    }

    /**
     * 获取当前登录用户
     * <p>
     * 可以匿名登陆的接口并且用户未登录获取的是null
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
        return Optional.ofNullable(UserHolder.get())
                .map(UserDTO::getId)
                .orElse(null);
    }

    /**
     * 获取当前登录用户username
     *
     * @return username
     */
    public static String getUserName() {
        return Optional.ofNullable(UserHolder.get())
                .map(UserDTO::getUsername)
                .orElse(null);
    }

    /**
     * 是否为 管理员
     *
     * @return true 管理员
     */
    public static boolean isAdministrator() {
        UserDTO user = UserHolder.get();
        if (user == null) {
            return false;
        }
        Integer roleType = user.getRoleType();
        return RoleType.isAdministrator(roleType);
    }

    /**
     * 检查是否为管理员权限
     */
    public static void requireAdministrator() {
        if (!isAdministrator()) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.NO_PERMISSION));
        }
    }

}
