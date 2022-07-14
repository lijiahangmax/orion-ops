package com.orion.ops.config;

import com.orion.ops.interceptor.LogPrintInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 日志打印配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/14 18:24
 */
@Configuration
@ConditionalOnBean(LogPrintInterceptor.class)
public class LogPrintConfig {

    @Value("${log.interceptor.expression:}")
    private String logInterceptorExpression;

    @Resource
    private LogPrintInterceptor logPrintInterceptor;

    @Bean
    @ConditionalOnProperty(name = "log.interceptor.expression")
    public AspectJExpressionPointcutAdvisor logAdvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(logInterceptorExpression);
        advisor.setAdvice(logPrintInterceptor);
        return advisor;
    }

}
