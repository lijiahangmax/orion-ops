package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.RpcWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.RoleType;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.HeadPicHolder;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.io.FileWriters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/25 19:11
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoDAO userInfoDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public RpcWrapper<Long> addUser(UserInfoRequest request) {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername());
        UserInfoDO query = userInfoDAO.selectOne(wrapper);
        if (query != null) {
            return RpcWrapper.error("用户名已存在");
        }
        // 角色判断
        RoleType role = RoleType.of(request.getRoleType());
        if (RoleType.SUPER_ADMINISTRATOR.equals(role)) {
            return RpcWrapper.error("不支持创建该角色");
        }
        if (RoleType.ADMINISTRATOR.equals(role) && !Currents.isSuperAdministrator()) {
            return RpcWrapper.error("不支持创建该角色");
        }
        // 密码
        String salt = UUIds.random19();
        String password = ValueMix.encPassword(request.getPassword(), salt);
        // 创建
        UserInfoDO insert = new UserInfoDO();
        insert.setUsername(request.getUsername());
        insert.setNickname(request.getNickname());
        insert.setPassword(password);
        insert.setSalt(salt);
        insert.setRoleType(request.getRoleType());
        insert.setUserStatus(Const.ENABLE);
        insert.setContactPhone(request.getContactPhone());
        insert.setContactEmail(request.getContactEmail());
        userInfoDAO.insert(insert);
        Long userId = insert.getId();
        // 生成头像
        String headPic = HeadPicHolder.generatorUserHeadPic(userId, request.getNickname());
        UserInfoDO update = new UserInfoDO();
        update.setId(userId);
        update.setHeadPic(headPic);
        userInfoDAO.updateById(update);
        return RpcWrapper.success(userId);
    }

    @Override
    public RpcWrapper<Integer> updateUser(UserInfoRequest request) {
        Long updateId;
        boolean updateCurrent = true;
        if (request.getId() != null) {
            updateId = request.getId();
            updateCurrent = false;
        } else {
            updateId = Currents.getUserId();
        }
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(updateId);
        if (userInfo == null) {
            return RpcWrapper.error("未查询到用户信息");
        }
        UserInfoDO update = new UserInfoDO();
        update.setId(updateId);
        Optional.ofNullable(request.getNickname())
                .filter(Strings::isNotBlank)
                .ifPresent(update::setNickname);
        Optional.ofNullable(request.getContactPhone())
                .filter(Strings::isNotBlank)
                .ifPresent(update::setContactPhone);
        Optional.ofNullable(request.getContactEmail())
                .filter(Strings::isNotBlank)
                .ifPresent(update::setContactEmail);
        Optional.ofNullable(request.getUserStatus()).ifPresent(update::setUserStatus);
        RoleType roleType = RoleType.of(request.getRoleType());
        if (roleType != null && !updateCurrent && Currents.isAdministrator()) {
            if (RoleType.SUPER_ADMINISTRATOR.equals(roleType)) {
                return RpcWrapper.error("不支持修改该角色");
            }
            if (RoleType.ADMINISTRATOR.equals(roleType) && !Currents.isSuperAdministrator()) {
                return RpcWrapper.error("不支持修改该角色");
            }
            update.setRoleType(roleType.getType());
        }
        int effect = userInfoDAO.updateById(update);
        // 更新token
        String cacheKey = Strings.format(KeyConst.LOGIN_TOKEN_KEY, updateId);
        String tokenValue = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isEmpty(tokenValue)) {
            return RpcWrapper.success(effect);
        }
        UserDTO userDTO = JSON.parseObject(tokenValue, UserDTO.class);
        Optional.ofNullable(roleType)
                .map(RoleType::getType)
                .ifPresent(userDTO::setRoleType);
        Optional.ofNullable(request.getNickname())
                .filter(Strings::isNotBlank)
                .ifPresent(userDTO::setNickname);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(userDTO), Const.LOGIN_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return RpcWrapper.success(effect);
    }

    @Override
    public Integer updateHeadPic(String headPic) {
        Long userId = Currents.getUserId();
        UserInfoDO userInfo = userInfoDAO.selectById(userId);
        // 删除原图片
        if (!Strings.isBlank(userInfo.getHeadPic())) {
            HeadPicHolder.deletePic(userInfo.getHeadPic());
        }
        // 写入图片
        String type = Base64s.img64Type(headPic);
        String url = HeadPicHolder.getPicPath(userId, type);
        byte[] pic = Base64s.img64Decode(headPic);
        String fullPicPath = HeadPicHolder.touchPicFile(userId, type);
        FileWriters.writeFast(fullPicPath, pic);
        // 更新
        UserInfoDO update = new UserInfoDO();
        update.setId(userId);
        update.setHeadPic(url);
        return userInfoDAO.updateById(update);
    }

}
