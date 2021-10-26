package com.orion.ops.aspect;

import com.alibaba.fastjson.JSON;
import com.orion.id.UUIds;
import com.orion.ops.consts.UserHolder;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Exceptions;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Optional;

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
public class LogAspect {

    /**
     * 请求id
     */
    private ThreadLocal<String> seq = ThreadLocal.withInitial(UUIds::random32);

    /**
     * 开始时间
     */
    private ThreadLocal<Date> start = ThreadLocal.withInitial(Date::new);

    @Pointcut("execution (* com.orion.ops.controller.*.*(..))")
    public void controllerLog() {
    }

    @Before("controllerLog()")
    public void beforeLogPrint(JoinPoint point) {
        StringBuilder requestLog = new StringBuilder("\napi请求-开始-seq: ").append(seq.get()).append('\n');
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
        requestLog.append("\t开始时间: ").append(Dates.format(start.get(), Dates.YMD_HMSS)).append('\n')
                .append("\tSignature: ").append(point.getSignature().getDeclaringTypeName()).append('.')
                .append(point.getSignature().getName()).append("()\n")
                .append("\t请求参数: ").append(argsToString(point.getArgs()));
        log.info(requestLog.toString());
    }

    @AfterReturning(returning = "ret", pointcut = "controllerLog()")
    public void afterReturnLogPrint(Object ret) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-结束-seq: ").append(seq.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - start.get().getTime()).append("ms \n")
                .append("\t响应结果: ").append(argsToString(ret));
        // 删除threadLocal
        seq.remove();
        start.remove();
        log.info(responseLog.toString());
    }

    @AfterThrowing(value = "controllerLog()", throwing = "throwable")
    public void doAfterThrowing(Throwable throwable) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-异常-seq: ").append(seq.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - start.get().getTime()).append("ms \n")
                .append("\t异常摘要: ").append(Exceptions.getDigest(throwable));
        // 删除threadLocal
        seq.remove();
        start.remove();
        log.error(responseLog.toString());
    }

    private String argsToString(Object o) {
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

}
