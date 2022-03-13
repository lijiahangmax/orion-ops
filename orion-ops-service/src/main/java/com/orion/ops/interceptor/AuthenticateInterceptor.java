package com.orion.ops.interceptor;

import com.orion.constant.StandardContentType;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.consts.user.UserHolder;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.Currents;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 17:20
 */
@Component
public class AuthenticateInterceptor implements HandlerInterceptor {

    @Resource
    private PassportService passportService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 是否跳过
        final boolean ignore = ((HandlerMethod) handler).hasMethodAnnotation(IgnoreAuth.class);
        HttpWrapper<?> rejectWrapper = null;
        String loginToken = Currents.getLoginToken(request);
        if (!Strings.isEmpty(loginToken)) {
            String ip = null;
            // 如果开启用户 ip 绑定 则获取 ip
            if (EnableType.of(SystemEnvAttr.LOGIN_IP_BIND.getValue()).getValue()) {
                ip = Servlets.getRemoteAddr(request);
            }
            // 获取用户登陆信息
            UserDTO user = passportService.getUserByToken(loginToken, ip);
            if (user != null) {
                if (Const.DISABLE.equals(user.getUserStatus())) {
                    rejectWrapper = HttpWrapper.of(ResultCode.USER_DISABLE);
                } else {
                    UserHolder.set(user);
                }
            } else {
                rejectWrapper = HttpWrapper.of(ResultCode.UNAUTHORIZED);
            }
        } else if (!ignore) {
            rejectWrapper = HttpWrapper.of(ResultCode.UNAUTHORIZED);
        }
        // 匿名接口直接返回
        if (ignore) {
            return true;
        }
        // 驳回接口设置返回
        if (rejectWrapper != null) {
            response.setContentType(StandardContentType.APPLICATION_JSON);
            Servlets.transfer(response, rejectWrapper.toJsonString().getBytes());
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.remove();
    }

}
