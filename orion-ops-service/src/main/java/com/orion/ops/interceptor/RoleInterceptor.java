package com.orion.ops.interceptor;

import com.orion.constant.StandardContentType;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.RoleType;
import com.orion.ops.consts.UserHolder;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Arrays1;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 21:31
 */
@Component
@Order(20)
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        RequireRole role = ((HandlerMethod) handler).getMethodAnnotation(RequireRole.class);
        if (role == null) {
            return true;
        }
        UserDTO user = UserHolder.get();
        if (user == null) {
            response.setContentType(StandardContentType.APPLICATION_JSON);
            Servlets.transfer(response, HttpWrapper.of(ResultCode.UNAUTHORIZED).toJsonString().getBytes());
            return false;
        }
        RoleType[] hasRoles = role.value();
        if (Arrays1.isEmpty(hasRoles)) {
            return true;
        }
        for (RoleType roleType : hasRoles) {
            if (roleType.getType().equals(user.getRoleType())) {
                return true;
            }
        }
        response.setContentType(StandardContentType.APPLICATION_JSON);
        Servlets.transfer(response, HttpWrapper.of(ResultCode.NO_PERMISSION).toJsonString().getBytes());
        return false;
    }

}
