package com.orion.ops.interceptor;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.UserEventLogService;
import com.orion.ops.utils.Currents;
import com.orion.servlet.web.Servlets;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用户操作日志切面
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 17:47
 */
@Component
@Aspect
@Slf4j
@Order(20)
public class UserEventLogAspect {

    @Resource
    private UserEventLogService userEventLogService;

    @Pointcut("@annotation(e)")
    public void eventLogPoint(EventLog e) {
    }

    @Before(value = "eventLogPoint(e)", argNames = "e")
    public void beforeLogRecord(EventLog e) {
        EventParamsHolder.remove();
        EventParamsHolder.addParam(EventKeys.INNER_REQUEST_SEQ, LogPrintInterceptor.SEQ_HOLDER.get());
        EventParamsHolder.addParam(EventKeys.INNER_REQUEST_TIME, Dates.current());
        // 有可能是登陆接口有可能为空 则用内部常量策略
        UserDTO user = Currents.getUser();
        if (user != null) {
            EventParamsHolder.addParam(EventKeys.INNER_USER_ID, user.getId());
            EventParamsHolder.addParam(EventKeys.INNER_USER_NAME, user.getUsername());
        }
        // 浏览器
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(s -> (ServletRequestAttributes) s)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    EventParamsHolder.addParam(EventKeys.INNER_REQUEST_USER_AGENT, Servlets.getUserAgent(request));
                    EventParamsHolder.addParam(EventKeys.INNER_REQUEST_IP, Servlets.getRemoteAddr(request));
                });
    }

    @AfterReturning(pointcut = "eventLogPoint(e)", argNames = "e")
    public void afterLogRecord(EventLog e) {
        userEventLogService.recordLog(e.value(), true);
    }

    @AfterThrowing(pointcut = "eventLogPoint(e)", argNames = "e")
    public void afterLogRecordThrowing(EventLog e) {
        userEventLogService.recordLog(e.value(), false);
    }

}
