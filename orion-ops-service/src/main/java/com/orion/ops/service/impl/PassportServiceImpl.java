package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.RpcWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.consts.RoleType;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserLoginRequest;
import com.orion.ops.entity.request.UserResetRequest;
import com.orion.ops.entity.vo.UserLoginVO;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.HeadPicHolder;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 认证
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

    @Override
    public RpcWrapper<UserLoginVO> login(UserLoginRequest request) {
        LambdaQueryWrapper<UserInfoDO> query = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername());
        UserInfoDO userInfo = userInfoDAO.selectOne(query);
        // 未找到用户
        if (userInfo == null) {
            return RpcWrapper.error("用户名或密码错误");
        }
        // 检查密码
        boolean validPassword = ValueMix.validPassword(request.getPassword(), userInfo.getSalt(), userInfo.getPassword());
        // 密码错误
        if (!validPassword) {
            return RpcWrapper.error("用户名或密码错误");
        }
        if (Const.DISABLE.equals(userInfo.getUserStatus())) {
            return RpcWrapper.error("用户已被禁用");
        }
        Long userId = userInfo.getId();
        UserInfoDO updateUser = new UserInfoDO();
        updateUser.setId(userId);
        // 检查头像
        if (Strings.isBlank(userInfo.getHeadPic())) {
            String url = HeadPicHolder.generatorUserHeadPic(userId, userInfo.getNickname());
            userInfo.setHeadPic(url);
            updateUser.setHeadPic(url);
        }
        // 更新最后登录时间
        updateUser.setLastLoginTime(new Date());
        userInfoDAO.updateById(updateUser);
        // 设置token
        long timestamp = System.currentTimeMillis();
        String loginToken = ValueMix.createLoginToken(userId, timestamp);
        UserDTO userCache = new UserDTO();
        userCache.setId(userId);
        userCache.setUsername(userInfo.getUsername());
        userCache.setNickname(userInfo.getNickname());
        userCache.setRoleType(userInfo.getRoleType());
        userCache.setTimestamp(timestamp);
        redisTemplate.opsForValue().set(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId),
                JSON.toJSONString(userCache), Const.LOGIN_TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 返回
        UserLoginVO loginInfo = new UserLoginVO();
        loginInfo.setToken(loginToken);
        loginInfo.setUserId(userId);
        loginInfo.setUsername(userInfo.getUsername());
        loginInfo.setNickname(userInfo.getNickname());
        loginInfo.setRoleType(userInfo.getRoleType());
        // 头像
        loginInfo.setHeadPic(HeadPicHolder.getBase64(userInfo.getHeadPic()));
        return RpcWrapper.success(loginInfo);
    }

    @Override
    public void logout() {
        UserDTO user = Currents.getUser();
        if (user == null) {
            return;
        }
        redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, user.getId()));
    }

    @Override
    public RpcWrapper<Boolean> resetPassword(UserResetRequest request) {
        String username = request.getUsername();
        boolean updateCurrent = Strings.isBlank(username);
        UserDTO current = Currents.getUser();
        if (!updateCurrent && current.getUsername().equals(username.trim())) {
            updateCurrent = true;
        }
        if (updateCurrent) {
            username = current.getUsername();
        } else {
            username = username.trim();
            // 检查权限
            if (!Currents.isAdministrator()) {
                return RpcWrapper.of(ResultCode.NO_PERMISSION);
            }
        }
        // 查询用户
        LambdaQueryWrapper<UserInfoDO> query = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, username);
        UserInfoDO userInfo = userInfoDAO.selectOne(query);
        if (userInfo == null) {
            return RpcWrapper.error("未查询到用户信息");
        }
        RoleType updateRoleType = RoleType.of(userInfo.getRoleType());
        // 检查是否更新的是超级管理员的密码
        if (RoleType.SUPER_ADMINISTRATOR.equals(updateRoleType) && !updateCurrent) {
            return RpcWrapper.of(ResultCode.NO_PERMISSION);
        }
        // 检查是否更新的是管理员的密码
        if (RoleType.ADMINISTRATOR.equals(updateRoleType) && (!updateCurrent || !Currents.isSuperAdministrator())) {
            return RpcWrapper.of(ResultCode.NO_PERMISSION);
        }

        // 修改密码
        String newPassword = ValueMix.encPassword(request.getPassword(), userInfo.getSalt());
        UserInfoDO updateUser = new UserInfoDO();
        Long userId = userInfo.getId();
        updateUser.setId(userId);
        updateUser.setPassword(newPassword);
        userInfoDAO.updateById(updateUser);
        // 删除token
        redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId));
        return RpcWrapper.success(!updateCurrent);
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
