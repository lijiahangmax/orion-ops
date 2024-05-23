package com.orion.ops.interceptor;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.service.api.UserEventLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
@Order(30)
public class UserEventLogAspect {

    @Resource
    private UserEventLogService userEventLogService;

    @Pointcut("@annotation(e)")
    public void eventLogPoint(EventLog e) {
    }

    @Before(value = "eventLogPoint(e)", argNames = "e")
    public void beforeLogRecord(EventLog e) {
        EventParamsHolder.remove();
        // 设置默认参数
        EventParamsHolder.setDefaultEventParams();
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
