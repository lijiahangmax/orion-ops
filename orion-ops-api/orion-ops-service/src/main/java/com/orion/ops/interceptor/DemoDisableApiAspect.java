package com.orion.ops.interceptor;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 演示模式禁用 api 切面
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/5/21 16:52
 */
@Aspect
@Slf4j
@Order(20)
public class DemoDisableApiAspect {

    public DemoDisableApiAspect() {
    }

    @Around("@annotation(o)")
    public Object around(ProceedingJoinPoint joinPoint, DemoDisableApi o) {
        throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.DEMO_DISABLE_API));
    }

}
