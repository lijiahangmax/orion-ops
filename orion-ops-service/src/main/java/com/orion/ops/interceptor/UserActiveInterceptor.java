package com.orion.ops.interceptor;

import com.orion.ops.consts.KeyConst;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.utils.Currents;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
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
     * 活跃域值 2h
     */
    private static final long ACTIVE_THRESHOLD = 1000 * 60 * 60 * 2L;

    /**
     * key: 用户id
     * value: 活跃时间戳
     */
    private Map<Long, Long> activeUsers = Maps.newCurrentHashMap();

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserInfoDAO userInfoDAO;

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
        Long userId = Currents.getUserId();
        if (userId == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Long before = activeUsers.put(userId, now);
        if (before == null) {
            // 应用启动但是token有效
            this.refreshActive(userId);
            return;
        }
        if (before + ACTIVE_THRESHOLD < now) {
            // 超过活跃域值
            this.refreshActive(userId);
        }
    }

    /**
     * 刷新活跃
     *
     * @param userId userId
     */
    private void refreshActive(Long userId) {
        // 刷新登陆token
        String loginKey = Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId);
        redisTemplate.expire(loginKey, KeyConst.LOGIN_TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 刷新登陆时间
        userInfoDAO.updateLastLoginTime(userId);
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
