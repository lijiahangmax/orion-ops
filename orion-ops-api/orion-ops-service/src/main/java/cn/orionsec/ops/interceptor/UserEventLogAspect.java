/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.interceptor;

import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.service.api.UserEventLogService;
import cn.orionsec.ops.utils.EventParamsHolder;
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
