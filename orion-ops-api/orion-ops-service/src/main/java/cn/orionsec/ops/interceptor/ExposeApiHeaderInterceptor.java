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
import cn.orionsec.kit.web.servlet.web.Servlets;
import cn.orionsec.ops.annotation.IgnoreCheck;
import cn.orionsec.ops.constant.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 暴露api请求头拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 16:09
 */
@Component
public class ExposeApiHeaderInterceptor implements HandlerInterceptor {

    @Value("${expose.api.access.header}")
    private String accessHeader;

    @Value("${expose.api.access.secret}")
    private String accessSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 是否跳过
        final boolean ignore = ((HandlerMethod) handler).hasMethodAnnotation(IgnoreCheck.class);
        if (ignore) {
            return true;
        }
        final boolean access = accessSecret.equals(request.getHeader(accessHeader));
        if (!access) {
            response.setContentType(StandardContentType.APPLICATION_JSON);
            Servlets.transfer(response, HttpWrapper.of(ResultCode.ILLEGAL_ACCESS).toJsonString().getBytes());
        }
        return access;
    }

}
