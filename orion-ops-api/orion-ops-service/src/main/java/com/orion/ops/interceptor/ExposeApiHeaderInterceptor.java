package com.orion.ops.interceptor;

import com.orion.lang.constant.StandardContentType;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreCheck;
import com.orion.ops.constant.ResultCode;
import com.orion.web.servlet.web.Servlets;
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
