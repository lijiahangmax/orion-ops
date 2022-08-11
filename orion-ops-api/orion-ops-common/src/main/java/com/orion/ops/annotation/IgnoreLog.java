package com.orion.ops.annotation;

import java.lang.annotation.*;

/**
 * 不执行统一日志打印
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.interceptor.LogPrintInterceptor
 * @since 2022/4/20 10:33
 */
@SuppressWarnings("ALL")
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreLog {
}
