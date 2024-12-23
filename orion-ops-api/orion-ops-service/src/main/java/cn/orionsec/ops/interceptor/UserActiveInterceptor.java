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

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.UserInfoDAO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.service.api.UserEventLogService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.EventParamsHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户活跃拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 14:58
 */
@Component
public class UserActiveInterceptor implements HandlerInterceptor {

    /**
     * key: 用户id
     * value: 活跃时间戳
     */
    private final Map<Long, Long> activeUsers = Maps.newCurrentHashMap();

    @Resource
    private UserInfoDAO userInfoDAO;

    @Resource
    private UserEventLogService userEventLogService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 处理
        this.handleActive();
        return true;
    }

    /**
     * 处理用户活跃信息
     */
    private void handleActive() {
        UserDTO user = Currents.getUser();
        if (user == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Long before = activeUsers.put(user.getId(), now);
        if (before == null) {
            // 应用启动/多端登录(单端登出) token有效
            this.refreshActive(user);
            return;
        }
        // 获取活跃域值
        long activeThresholdHour = Long.parseLong(SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW_THRESHOLD.getValue());
        long activeThreshold = TimeUnit.HOURS.toMillis(activeThresholdHour);
        if (before + activeThreshold < now) {
            // 超过活跃域值
            this.refreshActive(user);
        }
    }

    /**
     * 刷新活跃
     *
     * @param user user
     */
    private void refreshActive(UserDTO user) {
        Long userId = user.getId();
        // 刷新登录时间
        userInfoDAO.updateLastLoginTime(userId);
        // 记录日志
        EventParamsHolder.addParam(EventKeys.REFRESH_LOGIN, Const.ENABLE);
        EventParamsHolder.setDefaultEventParams();
        userEventLogService.recordLog(EventType.LOGIN, true);
        // 如果开启自动续签 刷新登录token 绑定token
        if (EnableType.of(SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW.getValue()).getValue()) {
            long expire = Long.parseLong(SystemEnvAttr.LOGIN_TOKEN_EXPIRE.getValue());
            String loginKey = Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId);
            String bindKey = Strings.format(KeyConst.LOGIN_TOKEN_BIND_KEY, userId, user.getCurrentBindTimestamp());
            redisTemplate.expire(loginKey, expire, TimeUnit.HOURS);
            redisTemplate.expire(bindKey, expire, TimeUnit.HOURS);
        }
    }

    /**
     * 设置活跃时间
     *
     * @param id   id
     * @param time 登录时间
     */
    public void setActiveTime(Long id, Long time) {
        activeUsers.put(id, time);
    }

    /**
     * 删除活跃时间
     *
     * @param id id
     */
    public void deleteActiveTime(Long id) {
        activeUsers.remove(id);
    }

}
