/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.annotation.IgnoreAuth;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.service.api.PassportService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.UserHolder;
import com.orion.lang.constant.StandardContentType;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Strings;
import com.orion.web.servlet.web.Servlets;
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
            // 获取用户登录信息
            UserDTO user = passportService.getUserByToken(loginToken, ip);
            if (user != null) {
                if (Const.DISABLE.equals(user.getUserStatus())) {
                    rejectWrapper = HttpWrapper.of(ResultCode.USER_DISABLED);
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
