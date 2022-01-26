package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.collect.MutableMap;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.dao.UserEventLogDAO;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.service.api.UserEventLogService;
import com.orion.ops.utils.EventLogUtils;
import com.orion.utils.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 用户日志实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 20:21
 */
@Service("userEventLogService")
public class UserEventLogServiceImpl implements UserEventLogService {

    @Resource
    private UserEventLogDAO userEventLogDAO;

    @Override
    public void recordLog(EventType eventType, boolean isSuccess) {
        // 获取以及移除参数
        MutableMap<String, Object> paramsMap = EventParamsHolder.get();
        EventParamsHolder.remove();
        // 判断是否保存
        if (!paramsMap.getBooleanValue(EventKeys.INNER_SAVE, true)) {
            return;
        }
        // 读取内置参数
        Long userId = paramsMap.getLong(EventKeys.INNER_USER_ID);
        if (userId == null) {
            return;
        }
        // 模板
        String template = paramsMap.getString(EventKeys.INNER_TEMPLATE, eventType.getTemplate());
        // 设置对象
        UserEventLogDO log = new UserEventLogDO();
        log.setUserId(userId);
        log.setUsername(paramsMap.getString(EventKeys.INNER_USER_NAME));
        log.setEventClassify(eventType.getClassify().getClassify());
        log.setEventType(eventType.getType());
        log.setLogInfo(Strings.format(template, paramsMap));
        // 移除内部key
        EventLogUtils.removeInnerKeys(paramsMap);
        log.setParamsJson(JSON.toJSONString(paramsMap));
        log.setExecResult(isSuccess ? Const.ENABLE : Const.DISABLE);
        log.setCreateTime(new Date());
        // 插入
        userEventLogDAO.insert(log);
    }

}
