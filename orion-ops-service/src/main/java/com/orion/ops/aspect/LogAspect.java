package com.orion.ops.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.orion.id.UUIds;
import com.orion.ops.consts.user.UserHolder;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Exceptions;
import com.orion.utils.collect.Sets;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * 统一日志打印
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/10/26 13:55
 */
@Component
@Aspect
@Slf4j
@Order(10)
public class LogAspect {

    /**
     * 忽略的日志字段
     * <p>
     * 简单实现 (用注解偏重)
     */
    private Set<String> ignoreLogFields = Sets.of("avatar");

    /**
     * 请求序列
     */
    public static final ThreadLocal<String> SEQ_HOLDER = ThreadLocal.withInitial(UUIds::random32);

    /**
     * 开始时间
     */
    private static final ThreadLocal<Date> START_HOLDER = ThreadLocal.withInitial(Date::new);

    @Pointcut("execution (* com.orion.ops.controller.*.*(..)) && !@annotation(com.orion.ops.annotation.IgnoreLog)")
    public void logPoint() {
    }

    @Before("logPoint()")
    public void beforeLogPrint(JoinPoint point) {
        StringBuilder requestLog = new StringBuilder("\napi请求-开始-seq: ").append(SEQ_HOLDER.get()).append('\n');
        // 登陆用户
        requestLog.append("\t当前用户: ").append(JSON.toJSONString(UserHolder.get())).append('\n');
        // http请求信息
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(s -> (ServletRequestAttributes) s)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    requestLog.append("\tUrl: ").append(Servlets.getMethod(request)).append(" ")
                            .append(Servlets.getRequestUrl(request)).append('\n')
                            .append("\tIP: ").append(Servlets.getRemoteAddr(request)).append('\n')
                            .append("\tQuery: ").append(Servlets.getQueryString(request)).append('\n')
                            .append("\tUA: ").append(Servlets.getUserAgent(request)).append('\n');
                });
        // 方法信息
        requestLog.append("\t开始时间: ").append(Dates.format(START_HOLDER.get(), Dates.YMD_HMSS)).append('\n')
                .append("\tSignature: ").append(point.getSignature().getDeclaringTypeName()).append('.')
                .append(point.getSignature().getName()).append("()\n")
                .append("\t请求参数: ").append(this.argsToString(point.getArgs()));
        log.info(requestLog.toString());
    }

    @AfterReturning(pointcut = "logPoint()", returning = "ret")
    public void afterReturnLogPrint(Object ret) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-结束-seq: ").append(SEQ_HOLDER.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - START_HOLDER.get().getTime()).append("ms \n")
                .append("\t响应结果: ").append(this.argsToString(ret));
        // 删除threadLocal
        SEQ_HOLDER.remove();
        START_HOLDER.remove();
        log.info(responseLog.toString());
    }

    @AfterThrowing(value = "logPoint()", throwing = "throwable")
    public void afterThrowingLogPrint(Throwable throwable) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-异常-seq: ").append(SEQ_HOLDER.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - START_HOLDER.get().getTime()).append("ms \n")
                .append("\t异常摘要: ").append(Exceptions.getDigest(throwable));
        // 删除threadLocal
        SEQ_HOLDER.remove();
        START_HOLDER.remove();
        log.error(responseLog.toString());
    }

    /**
     * 参数转json
     *
     * @param o object
     * @return json
     */
    private String argsToString(Object o) {
        try {
            return JSON.toJSONString(o, (PropertyFilter) (object, name, value) -> !ignoreLogFields.contains(name));
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

}
