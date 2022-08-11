package com.orion.ops.interceptor;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.EnableType;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.service.api.UserEventLogService;
import com.orion.ops.utils.Currents;
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
            // 应用启动/多端登陆(单端登出) token有效
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
        // 刷新登陆时间
        userInfoDAO.updateLastLoginTime(userId);
        // 记录日志
        EventParamsHolder.addParam(EventKeys.REFRESH_LOGIN, Const.ENABLE);
        EventParamsHolder.setDefaultEventParams();
        userEventLogService.recordLog(EventType.LOGIN, true);
        // 如果开启自动续签 刷新登陆token 绑定token
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
     * @param time 登陆时间
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
