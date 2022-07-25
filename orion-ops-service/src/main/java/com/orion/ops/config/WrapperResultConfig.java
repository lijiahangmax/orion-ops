package com.orion.ops.config;

import com.orion.lang.constant.StandardContentType;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.define.wrapper.RpcWrapper;
import com.orion.lang.utils.collect.Lists;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.web.servlet.web.Servlets;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * wrapper 统一返回包装配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 17:14
 */
@Configuration
public class WrapperResultConfig implements HandlerMethodReturnValueHandler {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @PostConstruct
    public void compare() {
        List<HandlerMethodReturnValueHandler> handlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> list = Lists.newList();
        list.add(this);
        if (handlers != null) {
            list.addAll(handlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(list);
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        // 统一返回值
        if (!methodParameter.getContainingClass().isAnnotationPresent(RestWrapper.class)) {
            return false;
        }
        return !methodParameter.hasMethodAnnotation(IgnoreWrapper.class);
        // && methodParameter.getExecutable().getAnnotatedReturnType().getType() != Void.TYPE;
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        if (response == null) {
            return;
        }
        HttpWrapper<?> wrapper;
        if (o instanceof HttpWrapper) {
            wrapper = (HttpWrapper<?>) o;
        } else if (o instanceof RpcWrapper) {
            wrapper = ((RpcWrapper<?>) o).toHttpWrapper();
        } else {
            wrapper = new HttpWrapper<>().data(o);
        }
        modelAndViewContainer.setRequestHandled(true);
        response.setContentType(StandardContentType.APPLICATION_JSON);
        Servlets.transfer(response, wrapper.toJsonString().getBytes());
    }

}
