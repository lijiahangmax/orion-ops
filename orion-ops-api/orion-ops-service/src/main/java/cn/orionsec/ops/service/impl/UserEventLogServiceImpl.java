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
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.define.collect.MutableMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.dao.UserEventLogDAO;
import cn.orionsec.ops.entity.domain.UserEventLogDO;
import cn.orionsec.ops.entity.request.user.EventLogRequest;
import cn.orionsec.ops.entity.vo.user.UserEventLogVO;
import cn.orionsec.ops.service.api.UserEventLogService;
import cn.orionsec.ops.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public DataGrid<UserEventLogVO> getLogList(EventLogRequest request) {
        if (Const.ENABLE.equals(request.getOnlyMyself())) {
            request.setUserId(Currents.getUserId());
        } else if (!Currents.isAdministrator()) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.NO_PERMISSION));
        }
        LambdaQueryWrapper<UserEventLogDO> wrapper = new LambdaQueryWrapper<UserEventLogDO>()
                .eq(Objects.nonNull(request.getUserId()), UserEventLogDO::getUserId, request.getUserId())
                .eq(Objects.nonNull(request.getClassify()), UserEventLogDO::getEventClassify, request.getClassify())
                .eq(Objects.nonNull(request.getType()), UserEventLogDO::getEventType, request.getType())
                .eq(Objects.nonNull(request.getResult()), UserEventLogDO::getExecResult, request.getResult())
                .like(Strings.isNotBlank(request.getUsername()), UserEventLogDO::getUsername, request.getUsername())
                .like(Strings.isNotBlank(request.getLog()), UserEventLogDO::getLogInfo, request.getLog())
                .like(Strings.isNotBlank(request.getParams()), UserEventLogDO::getParamsJson, request.getParams())
                .between(Objects1.isNoneNull(request.getRangeStart(), request.getRangeEnd()), UserEventLogDO::getCreateTime,
                        request.getRangeStart(), request.getRangeEnd())
                .orderByDesc(UserEventLogDO::getCreateTime);
        // 查询
        DataGrid<UserEventLogVO> logs = DataQuery.of(userEventLogDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(UserEventLogVO.class);
        if (!Const.ENABLE.equals(request.getParserIp())) {
            return logs;
        }
        // 解析ip
        logs.forEach(log -> {
            String ip = Optional.ofNullable(log.getParams())
                    .map(JSON::parseObject)
                    .map(s -> s.getString(EventKeys.INNER_REQUEST_IP))
                    .orElse(null);
            log.setIp(ip);
            log.setIpLocation(Utils.getIpLocation(ip));
        });
        return logs;
    }

}
