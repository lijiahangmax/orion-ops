package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.UserLoginRequest;
import com.orion.ops.entity.request.UserResetRequest;
import com.orion.ops.entity.vo.UserLoginVO;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.CookiesExt;
import com.orion.utils.Booleans;
import com.orion.utils.Objects1;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 17:05
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/auth")
public class AuthenticateController {

    @Resource
    private PassportService passportService;

    /**
     * 登录
     */
    @RequestMapping("/login")
    @IgnoreAuth
    @EventLog(EventType.LOGIN)
    public UserLoginVO login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        String username = Valid.notBlank(request.getUsername()).trim();
        String password = Valid.notBlank(request.getPassword()).trim();
        request.setUsername(username);
        request.setPassword(password);
        // 登录
        UserLoginVO data = passportService.login(request);
        // 设置cookie
        CookiesExt.set(response, Const.LOGIN_TOKEN, data.getToken(), KeyConst.LOGIN_TOKEN_EXPIRE);
        return data;
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    @IgnoreAuth
    @EventLog(EventType.LOGOUT)
    public HttpWrapper<?> logout(HttpServletResponse response) {
        passportService.logout();
        CookiesExt.delete(response, Const.LOGIN_TOKEN);
        return HttpWrapper.ok();
    }

    /**
     * 重置密码
     */
    @RequestMapping("/reset")
    @EventLog(EventType.RESET_PASSWORD)
    public HttpWrapper<?> resetPassword(@RequestBody UserResetRequest request, HttpServletResponse response) {
        String password = Valid.notBlank(request.getPassword()).trim();
        request.setUserId(Objects1.def(request.getUserId(), Currents::getUserId));
        request.setPassword(password);
        Boolean res = passportService.resetPassword(request);
        if (Booleans.isTrue(res)) {
            CookiesExt.delete(response, Const.LOGIN_TOKEN);
        }
        return HttpWrapper.ok();
    }

}
