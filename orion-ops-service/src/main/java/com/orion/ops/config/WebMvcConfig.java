package com.orion.ops.config;

import com.orion.exception.IORuntimeException;
import com.orion.exception.argument.CodeArgumentException;
import com.orion.exception.argument.HttpWrapperException;
import com.orion.exception.argument.InvalidArgumentException;
import com.orion.exception.argument.RpcWrapperException;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.interceptor.AuthenticateInterceptor;
import com.orion.ops.interceptor.RoleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public HttpWrapper<?> httpRequestExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return HttpWrapper.error().msg(MessageConst.INVALID_PARAM);
    }

    @ExceptionHandler(value = Exception.class)
    public HttpWrapper<?> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        return HttpWrapper.error().msg(MessageConst.EXCEPTION_MESSAGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidArgumentException.class, IllegalArgumentException.class})
    public HttpWrapper<?> invalidArgumentExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return HttpWrapper.error().msg(ex.getMessage());
    }

    @ExceptionHandler(value = {IOException.class, IORuntimeException.class})
    public HttpWrapper<?> ioExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return HttpWrapper.error().msg(MessageConst.IO_EXCEPTION_MESSAGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = CodeArgumentException.class)
    public HttpWrapper<?> codeArgumentExceptionHandler(CodeArgumentException ex) {
        return HttpWrapper.error().code(ex.getCode()).msg(ex.getMessage());
    }

    @ExceptionHandler(value = HttpWrapperException.class)
    public HttpWrapper<?> httpWrapperExceptionHandler(HttpWrapperException ex) {
        return ex.getWrapper();
    }

    @ExceptionHandler(value = RpcWrapperException.class)
    public HttpWrapper<?> rpcWrapperExceptionHandler(RpcWrapperException ex) {
        return ex.getWrapper().toHttpWrapper();
    }

}
