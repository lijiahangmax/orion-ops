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
package cn.orionsec.ops.config;

import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.interceptor.*;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.exception.*;
import com.orion.lang.exception.argument.CodeArgumentException;
import com.orion.lang.exception.argument.HttpWrapperException;
import com.orion.lang.exception.argument.InvalidArgumentException;
import com.orion.lang.exception.argument.RpcWrapperException;
import com.orion.lang.utils.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.sql.SQLException;

/**
 * spring mvc 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 10:24
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private IpFilterInterceptor ipFilterInterceptor;

    @Resource
    private AuthenticateInterceptor authenticateInterceptor;

    @Resource
    private RoleInterceptor roleInterceptor;

    @Resource
    private UserActiveInterceptor userActiveInterceptor;

    @Resource
    private ExposeApiHeaderInterceptor exposeApiHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // IP拦截器
        registry.addInterceptor(ipFilterInterceptor)
                .addPathPatterns("/**")
                .order(5);
        // 认证拦截器
        registry.addInterceptor(authenticateInterceptor)
                .addPathPatterns("/orion/api/**")
                .order(10);
        // 权限拦截器
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/orion/api/**")
                .order(20);
        // 活跃拦截器
        registry.addInterceptor(userActiveInterceptor)
                .addPathPatterns("/orion/api/**")
                .excludePathPatterns("/orion/api/auth/**")
                .order(30);
        // 暴露服务请求头拦截器
        registry.addInterceptor(exposeApiHeaderInterceptor)
                .addPathPatterns("/orion/expose-api/**")
                .order(40);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 暴露服务允许跨域
        registry.addMapping("/orion/expose-api/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * @return 演示模式禁用 api 切面
     */
    @Bean
    @ConditionalOnProperty(value = "demo.mode", havingValue = "true")
    public DemoDisableApiAspect demoDisableApiAspect() {
        return new DemoDisableApiAspect();
    }

    @ExceptionHandler(value = Exception.class)
    public HttpWrapper<?> normalExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("normalExceptionHandler url: {}, 抛出异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.EXCEPTION_MESSAGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = ApplicationException.class)
    public HttpWrapper<?> applicationExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("applicationExceptionHandler url: {}, 抛出异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(ex.getMessage());
    }

    @ExceptionHandler(value = DataAccessResourceFailureException.class)
    public HttpWrapper<?> dataAccessResourceFailureExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("dataAccessResourceFailureExceptionHandler url: {}, 抛出异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.NETWORK_FLUCTUATION);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, BindException.class})
    public HttpWrapper<?> httpRequestExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("httpRequestExceptionHandler url: {}, http请求异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.INVALID_PARAM);
    }

    @ExceptionHandler(value = {HttpRequestException.class})
    public HttpWrapper<?> httpApiRequestExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("httpApiRequestExceptionHandler url: {}, http-api请求异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.HTTP_API);
    }

    @ExceptionHandler(value = {InvalidArgumentException.class, IllegalArgumentException.class, DisabledException.class})
    public HttpWrapper<?> invalidArgumentExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("invalidArgumentExceptionHandler url: {}, 参数异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(ex.getMessage());
    }

    @ExceptionHandler(value = {IOException.class, IORuntimeException.class})
    public HttpWrapper<?> ioExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("ioExceptionHandler url: {}, io异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.IO_EXCEPTION_MESSAGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)
    public HttpWrapper<?> sqlExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("sqlExceptionHandler url: {}, sql异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.SQL_EXCEPTION_MESSAGE);
    }

    @ExceptionHandler(value = {SftpException.class, com.jcraft.jsch.SftpException.class})
    public HttpWrapper<?> sftpExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("sftpExceptionHandler url: {}, sftp处理异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.OPERATOR_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = ParseRuntimeException.class)
    public HttpWrapper<?> parseExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("parseExceptionHandler url: {}, 解析异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        if (Exceptions.isCausedBy(ex, EncryptedDocumentException.class)) {
            // excel 密码错误
            return HttpWrapper.error(MessageConst.OPEN_TEMPLATE_ERROR).data(ex.getMessage());
        } else {
            return HttpWrapper.error(MessageConst.PARSE_TEMPLATE_DATA_ERROR).data(ex.getMessage());
        }
    }

    @ExceptionHandler(value = EncryptException.class)
    public HttpWrapper<?> encryptExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("encryptExceptionHandler url: {}, 数据加密异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.ENCRYPT_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = DecryptException.class)
    public HttpWrapper<?> decryptExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("decryptExceptionHandler url: {}, 数据解密异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.DECRYPT_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = VcsException.class)
    public HttpWrapper<?> vcsExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("vcsExceptionHandler url: {}, vcs处理异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.REPOSITORY_OPERATOR_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = {TaskExecuteException.class, ExecuteException.class})
    public HttpWrapper<?> taskExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("taskExceptionHandler url: {}, task处理异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.TASK_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = ConnectionRuntimeException.class)
    public HttpWrapper<?> connectionExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("connectionExceptionHandler url: {}, connect异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.CONNECT_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = {TimeoutException.class, java.util.concurrent.TimeoutException.class})
    public HttpWrapper<?> timeoutExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("timeoutExceptionHandler url: {}, timeout异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.TIMEOUT_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = {InterruptedException.class, InterruptedRuntimeException.class, InterruptedIOException.class})
    public HttpWrapper<?> interruptExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("interruptExceptionHandler url: {}, interrupt异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.INTERRUPT_ERROR).data(ex.getMessage());
    }

    @ExceptionHandler(value = UnsafeException.class)
    public HttpWrapper<?> unsafeExceptionHandler(HttpServletRequest request, Exception ex) {
        log.error("unsafeExceptionHandler url: {}, unsafe异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.UNSAFE_OPERATOR).data(ex.getMessage());
    }

    @ExceptionHandler(value = LogException.class)
    public HttpWrapper<?> logExceptionHandler(HttpServletRequest request, LogException ex) {
        log.error("logExceptionHandler url: {}, 处理异常打印日志: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.EXCEPTION_MESSAGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = ParseCronException.class)
    public HttpWrapper<?> parseCronExceptionHandler(ParseCronException ex) {
        return HttpWrapper.error(MessageConst.ERROR_EXPRESSION).data(ex.getMessage());
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public HttpWrapper<?> maxUploadSizeExceededExceptionHandler(HttpServletRequest request, MaxUploadSizeExceededException ex) {
        log.error("maxUploadSizeExceededExceptionHandler url: {}, 上传异常: {}, message: {}", request.getRequestURI(), ex.getClass(), ex.getMessage(), ex);
        return HttpWrapper.error(MessageConst.FILE_TOO_LARGE).data(ex.getMessage());
    }

    @ExceptionHandler(value = CodeArgumentException.class)
    public HttpWrapper<?> codeArgumentExceptionHandler(CodeArgumentException ex) {
        return HttpWrapper.error(ex.getCode(), ex.getMessage());
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
