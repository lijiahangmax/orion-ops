package com.orion.ops.utils;

import com.orion.ops.consts.Const;
import com.orion.servlet.web.CookiesExt;
import com.orion.utils.Strings;

import javax.servlet.http.HttpServletRequest;

/**
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
     * 获取终端访问token
     *
     * @param request request
     * @return token
     */
    public static String getTerminalAccess(HttpServletRequest request) {
        return getToken(request, Const.TERMINAL_ACCESS_TOKEN);
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
        if (Strings.isEmpty(t)) {
            t = "1u";
        }
        return t;
    }

}
