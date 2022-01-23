package com.orion.ops.aspect;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.dao.UserEventLogDAO;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.utils.Currents;
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
@Order(20)
public class UserEventLogAspect {

    @Resource
    private UserEventLogDAO userEventLogDAO;

    @Pointcut("@annotation(e)")
    public void eventLogPoint(EventLog e) {
    }

    @Before(value = "eventLogPoint(e)", argNames = "e")
    public void beforeLogRecord(EventLog e) {
        // 有可能是登陆接口有可能为空 则用内部常量策略
        UserDTO user = Currents.getUser();
        if (user != null) {
            e.value().addParam(EventKeys.INNER_USER_ID, user.getId());
            e.value().addParam(EventKeys.INNER_USER_NAME, user.getUsername());
        }
    }

    @AfterReturning(pointcut = "eventLogPoint(e)", argNames = "e")
    public void afterLogRecord(EventLog e) {
        UserEventLogDO log = e.value().getEventLog();
        if (log == null) {
            return;
        }
        userEventLogDAO.insert(log);
    }

    @AfterThrowing(pointcut = "eventLogPoint(e)", argNames = "e")
    public void afterLogRecordThrowing(EventLog e) {
        e.value().remove();
    }

}
