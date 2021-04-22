package com.orion.ops.config;

import com.orion.exception.AuthenticationException;
import com.orion.exception.ConnectionRuntimeException;
import com.orion.exception.IORuntimeException;
import com.orion.exception.argument.CodeArgumentException;
import com.orion.exception.argument.InvalidArgumentException;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.interceptor.AuthenticateInterceptor;
import com.orion.ops.interceptor.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * spring mvc 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 10:24
 */
@Configuration
@RestControllerAdvice
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthenticateInterceptor authenticateInterceptor;

    @Resource
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authenticateInterceptor)
                .addPathPatterns("/orion/api/**");
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/orion/api/**");
    }


    @ExceptionHandler(value = Exception.class)
    public HttpWrapper<?> exceptionHandler(Exception ex) {
        return HttpWrapper.error().msg("系统繁忙").data(ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidArgumentException.class, IllegalArgumentException.class})
    public HttpWrapper<?> invalidArgumentExceptionHandler(Exception ex) {
        return HttpWrapper.error().msg(ex.getMessage());
    }

    @ExceptionHandler(value = CodeArgumentException.class)
    public HttpWrapper<?> codeArgumentExceptionHandler(CodeArgumentException ex) {
        return HttpWrapper.error().code(ex.getCode()).msg(ex.getMessage());
    }

    @ExceptionHandler(value = {IOException.class, IORuntimeException.class})
    public HttpWrapper<?> ioExceptionHandler(Exception ex) {
        return HttpWrapper.error().msg("网络异常").data(ex.getMessage());
    }

    @ExceptionHandler(value = {ConnectionRuntimeException.class})
    public HttpWrapper<?> connectionExceptionHandler(Exception ex) {
        return HttpWrapper.error().msg("连接失败").data(ex.getMessage());
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public HttpWrapper<?> authExceptionHandler(Exception ex) {
        return HttpWrapper.error().msg("认证失败").data(ex.getMessage());
    }

}
