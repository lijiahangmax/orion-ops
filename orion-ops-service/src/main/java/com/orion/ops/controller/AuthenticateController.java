package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.lang.wrapper.RpcWrapper;
import com.orion.lang.wrapper.Wrapper;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.entity.request.UserLoginRequest;
import com.orion.ops.entity.request.UserResetRequest;
import com.orion.ops.entity.vo.UserLoginVO;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.CookiesExt;
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
    public Wrapper<UserLoginVO> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        String username = Valid.notBlank(request.getUsername()).trim();
        String password = Valid.notBlank(request.getPassword()).trim();
        request.setUsername(username);
        request.setPassword(password);
        RpcWrapper<UserLoginVO> res = passportService.login(request);
        // 登录失败
        if (!res.isSuccess()) {
            return HttpWrapper.of(ResultCode.LOGIN_FAILURE);
        }
        // 登录成功
        UserLoginVO data = res.getData();
        CookiesExt.set(response, Const.LOGIN_TOKEN, data.getToken(), Const.LOGIN_TOKEN_EXPIRE);
        return HttpWrapper.ok(data);
    }

    /**
     * 登出
     */
    @RequestMapping("/logout")
    @IgnoreAuth
    public Wrapper<?> logout(HttpServletResponse response) {
        passportService.logout();
        CookiesExt.delete(response, Const.LOGIN_TOKEN);
        return HttpWrapper.ok();
    }

    /**
     * 重置密码
     */
    @RequestMapping("/reset")
    public Wrapper<?> resetPassword(@RequestBody UserResetRequest request, HttpServletResponse response) {
        String password = Valid.notBlank(request.getPassword()).trim();
        request.setPassword(password);
        RpcWrapper<Boolean> res = passportService.resetPassword(request);
        if (!res.isSuccess()) {
            return HttpWrapper.error(res.getMsg());
        }
        if (res.getData()) {
            CookiesExt.delete(response, Const.LOGIN_TOKEN);
        }
        return HttpWrapper.ok();
    }

}
