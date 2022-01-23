package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserLoginRequest;
import com.orion.ops.entity.request.UserResetRequest;
import com.orion.ops.entity.vo.UserLoginVO;
import com.orion.ops.interceptor.UserActiveInterceptor;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.AvatarPicHolder;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/16 23:34
 */
@Service("passportService")
public class PassportServiceImpl implements PassportService {

    @Resource
    private UserInfoDAO userInfoDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserActiveInterceptor userActiveInterceptor;

    @Override
    @EventLog(EventType.LOGIN)
    public UserLoginVO login(UserLoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<UserInfoDO> query = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername())
                .last(Const.LIMIT_1);
        UserInfoDO userInfo = userInfoDAO.selectOne(query);
        Valid.notNull(userInfo, MessageConst.USERNAME_PASSWORD_ERROR);
        // 检查密码
        boolean validPassword = ValueMix.validPassword(request.getPassword(), userInfo.getSalt(), userInfo.getPassword());
        // 密码错误
        Valid.isTrue(validPassword, MessageConst.USERNAME_PASSWORD_ERROR);
        Valid.isTrue(Const.ENABLE.equals(userInfo.getUserStatus()), MessageConst.USER_DISABLED);
        Long userId = userInfo.getId();
        String username = userInfo.getUsername();
        UserInfoDO updateUser = new UserInfoDO();
        updateUser.setId(userId);
        // 检查头像
        if (!AvatarPicHolder.isExist(userInfo.getAvatarPic())) {
            String url = AvatarPicHolder.generatorUserAvatar(userId, userInfo.getNickname());
            userInfo.setAvatarPic(url);
            updateUser.setAvatarPic(url);
        }
        // 更新最后登录时间
        updateUser.setUpdateTime(new Date());
        updateUser.setLastLoginTime(new Date());
        userInfoDAO.updateById(updateUser);
        // 设置token
        long timestamp = System.currentTimeMillis();
        String loginToken = ValueMix.createLoginToken(userId, timestamp);
        UserDTO userCache = new UserDTO();
        userCache.setId(userId);
        userCache.setUsername(username);
        userCache.setNickname(userInfo.getNickname());
        userCache.setRoleType(userInfo.getRoleType());
        userCache.setTimestamp(timestamp);
        redisTemplate.opsForValue().set(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId), JSON.toJSONString(userCache),
                KeyConst.LOGIN_TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 返回
        UserLoginVO loginInfo = new UserLoginVO();
        loginInfo.setToken(loginToken);
        loginInfo.setUserId(userId);
        loginInfo.setUsername(username);
        loginInfo.setNickname(userInfo.getNickname());
        loginInfo.setRoleType(userInfo.getRoleType());
        // 设置活跃时间
        userActiveInterceptor.setActiveTime(userId, timestamp);
        // 设置操作日志参数
        EventType.LOGIN.addParam(EventKeys.INNER_USER_ID, userId);
        EventType.LOGIN.addParam(EventKeys.INNER_USER_NAME, username);
        return loginInfo;
    }

    @Override
    @EventLog(EventType.LOGOUT)
    public void logout() {
        Long userId = Currents.getUserId();
        if (userId == null) {
            return;
        }
        // 删除token
        redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId));
        // 删除活跃时间
        userActiveInterceptor.deleteActiveTime(userId);
    }

    @Override
    @EventLog(EventType.RESET_PASSWORD)
    public Boolean resetPassword(UserResetRequest request) {
        UserDTO current = Currents.getUser();
        Long updateUserId = request.getUserId();
        final boolean isAdmin = Currents.isAdministrator();
        final boolean updateCurrent = current.getId().equals(updateUserId);
        // 检查权限
        if (!updateCurrent && !isAdmin) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.NO_PERMISSION));
        }
        // 查询更新用户
        UserInfoDO userInfo = userInfoDAO.selectById(updateUserId);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 检查原密码是否正确
        if (updateCurrent) {
            String beforePassword = Valid.notBlank(request.getBeforePassword(), MessageConst.BEFORE_PASSWORD_EMPTY);
            String validBeforePassword = ValueMix.encPassword(beforePassword, userInfo.getSalt());
            Valid.isTrue(validBeforePassword.equals(userInfo.getPassword()), MessageConst.BEFORE_PASSWORD_ERROR);
        }
        if (updateCurrent) {
            // 修改自己密码不记录
            EventType.RESET_PASSWORD.addParam(EventKeys.INNER_SAVE, false);
        } else {
            EventType.RESET_PASSWORD.addParam(EventKeys.TARGET_USERNAME, userInfo.getUsername());
        }
        // 修改密码
        String newPassword = ValueMix.encPassword(request.getPassword(), userInfo.getSalt());
        UserInfoDO updateUser = new UserInfoDO();
        Long userId = userInfo.getId();
        updateUser.setId(userId);
        updateUser.setPassword(newPassword);
        updateUser.setUpdateTime(new Date());
        userInfoDAO.updateById(updateUser);
        // 删除token
        redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId));
        // 删除活跃时间
        userActiveInterceptor.deleteActiveTime(userId);
        return updateCurrent;
    }

    @Override
    public UserDTO getUserByToken(String token) {
        if (Strings.isBlank(token)) {
            return null;
        }
        Long[] info = ValueMix.getLoginTokenInfo(token);
        if (info == null) {
            return null;
        }
        String cache = redisTemplate.opsForValue().get(Strings.format(KeyConst.LOGIN_TOKEN_KEY, info[0]));
        if (Strings.isEmpty(cache)) {
            return null;
        }
        UserDTO user = JSON.parseObject(cache, UserDTO.class);
        if (!info[1].equals(user.getTimestamp())) {
            return null;
        }
        return user;
    }

}
