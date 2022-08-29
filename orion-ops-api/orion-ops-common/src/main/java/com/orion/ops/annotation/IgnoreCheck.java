package com.orion.ops.annotation;

import java.lang.annotation.*;

/**
 * 无需检查注解
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 16:26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreCheck {
}
