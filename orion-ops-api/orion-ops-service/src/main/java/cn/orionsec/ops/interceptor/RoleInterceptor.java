/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.interceptor;

import cn.orionsec.kit.lang.constant.StandardContentType;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Arrays1;
import cn.orionsec.kit.web.servlet.web.Servlets;
import cn.orionsec.ops.annotation.RequireRole;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.utils.UserHolder;
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
