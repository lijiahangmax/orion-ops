package com.orion.ops.config;

import com.orion.ops.handler.WrapperResultHandler;
import com.orion.utils.collect.Lists;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 17:14
 */
@Configuration
public class WrapperResultConfig {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private WrapperResultHandler wrapperResultHandler;

    @PostConstruct
    public void compare() {
        List<HandlerMethodReturnValueHandler> handlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> list = Lists.newList();
        list.add(wrapperResultHandler);
        if (handlers != null) {
            list.addAll(handlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(list);
    }


}
