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

import cn.orionsec.ops.interceptor.LogPrintInterceptor;
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
