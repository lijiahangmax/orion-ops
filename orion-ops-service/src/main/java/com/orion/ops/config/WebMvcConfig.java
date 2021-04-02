package com.orion.ops.config;

import com.orion.exception.argument.InvalidArgumentException;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.interceptor.AuthenticateInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authenticateInterceptor)
                .addPathPatterns("/orion/api/**")
                .excludePathPatterns("/orion/auth/**");
    }


    @ExceptionHandler(value = Exception.class)
    public HttpWrapper<?> exceptionHandler(Exception ex) {
        return HttpWrapper.error().msg("系统繁忙").data(ex.getMessage());
    }

    @ExceptionHandler(value = InvalidArgumentException.class)
    public HttpWrapper<?> invalidArgumentExceptionHandler(Exception ex) {
        return HttpWrapper.error().msg(ex.getMessage());
    }

}
