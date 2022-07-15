package com.orion.ops.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.user.UserHolder;
import com.orion.web.servlet.web.Servlets;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Optional;

/**
 * 日志打印拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/14 18:25
 */
@Slf4j
@Order(10)
@Component
public class LogPrintInterceptor implements MethodInterceptor {

    @Value("#{'${log.interceptor.ignore.fields:}'.split(',')}")
    private String[] ignoreFields;

    /**
     * 请求序列
     */
    public static final ThreadLocal<String> SEQ_HOLDER = ThreadLocal.withInitial(UUIds::random32);

    /**
     * 开始时间
     */
    private static final ThreadLocal<Date> START_HOLDER = ThreadLocal.withInitial(Date::new);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 打印开始日志
        this.beforeLogPrint(invocation);
        try {
            // 执行方法
            Object ret = invocation.proceed();
            // 返回打印
            this.afterReturnLogPrint(ret);
            return ret;
        } catch (Throwable t) {
            // 异常打印
            this.afterThrowingLogPrint(t);
            throw t;
        } finally {
            // 删除threadLocal
            SEQ_HOLDER.remove();
            START_HOLDER.remove();
        }
    }

    /**
     * 方法进入打印
     *
     * @param invocation invocation
     */
    public void beforeLogPrint(MethodInvocation invocation) {
        StringBuilder requestLog = new StringBuilder("\napi请求-开始-seq: ").append(SEQ_HOLDER.get()).append('\n');
        // 登陆用户
        requestLog.append("\t当前用户: ").append(JSON.toJSONString(UserHolder.get())).append('\n');
        // http请求信息
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(s -> (ServletRequestAttributes) s)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    // url
                    requestLog.append("\t").append(Servlets.getMethod(request)).append(" ")
                            .append(Servlets.getRequestUrl(request)).append('\n');
                    // query
                    requestLog.append("\tip: ").append(Servlets.getRemoteAddr(request)).append('\n')
                            .append("\tquery: ").append(Servlets.getQueryString(request)).append('\n');
                    // header
                    Servlets.getHeaderMap(request).forEach((hk, hv) -> requestLog.append('\t')
                            .append(hk).append(": ")
                            .append(hv).append('\n'));
                });
        // 方法信息
        Method method = invocation.getMethod();
        requestLog.append("\t开始时间: ").append(Dates.format(START_HOLDER.get(), Dates.YMD_HMSS)).append('\n')
                .append("\t方法签名: ").append(method.getDeclaringClass().getName()).append('#')
                .append(method.getName()).append("\n")
                .append("\t请求参数: ").append(this.argsToString(invocation.getArguments()));
        log.info(requestLog.toString());
    }

    /**
     * 返回打印
     *
     * @param ret return
     */
    private void afterReturnLogPrint(Object ret) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-结束-seq: ").append(SEQ_HOLDER.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - START_HOLDER.get().getTime()).append("ms \n")
                .append("\t响应结果: ").append(this.argsToString(ret));
        log.info(responseLog.toString());
    }

    /**
     * 异常打印
     *
     * @param throwable ex
     */
    private void afterThrowingLogPrint(Throwable throwable) {
        Date endTime = new Date();
        // 响应日志
        StringBuilder responseLog = new StringBuilder("\napi请求-异常-seq: ").append(SEQ_HOLDER.get()).append('\n');
        responseLog.append("\t结束时间: ").append(Dates.format(endTime, Dates.YMD_HMSS))
                .append(" used: ").append(endTime.getTime() - START_HOLDER.get().getTime()).append("ms \n")
                .append("\t异常摘要: ").append(Exceptions.getDigest(throwable));
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
            if (ignoreFields.length == 1 && Const.EMPTY.equals(ignoreFields[0])) {
                // 不过滤
                return JSON.toJSONString(o);
            } else {
                return JSON.toJSONString(o, (PropertyFilter) (object, name, value) -> !Arrays1.contains(ignoreFields, name));

            }
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

}
