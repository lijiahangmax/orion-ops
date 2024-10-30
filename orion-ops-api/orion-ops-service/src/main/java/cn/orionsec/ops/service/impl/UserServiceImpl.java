/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.constant.CnConst;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.dao.UserInfoDAO;
import cn.orionsec.ops.entity.domain.UserInfoDO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.entity.request.user.UserInfoRequest;
import cn.orionsec.ops.entity.vo.user.UserInfoVO;
import cn.orionsec.ops.interceptor.UserActiveInterceptor;
import cn.orionsec.ops.service.api.AlarmGroupUserService;
import cn.orionsec.ops.service.api.UserService;
import cn.orionsec.ops.utils.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Objects1;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.codec.Base64s;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.crypto.Signatures;
import com.orion.lang.utils.io.FileWriters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
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

    private static final String DEFAULT_USERNAME = "orionadmin";

    private static final String DEFAULT_NICKNAME = "管理员";

    @Resource
    private UserInfoDAO userInfoDAO;

    @Resource
    private AlarmGroupUserService alarmGroupUserService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserActiveInterceptor userActiveInterceptor;

    @Override
    public DataGrid<UserInfoVO> userList(UserInfoRequest request) {
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(Objects.nonNull(request.getId()), UserInfoDO::getId, request.getId())
                .eq(Objects.nonNull(request.getRole()), UserInfoDO::getRoleType, request.getRole())
                .eq(Objects.nonNull(request.getStatus()), UserInfoDO::getUserStatus, request.getStatus())
                .like(Strings.isNotBlank(request.getUsername()), UserInfoDO::getUsername, request.getUsername())
                .like(Strings.isNotBlank(request.getNickname()), UserInfoDO::getNickname, request.getNickname())
                .like(Strings.isNotBlank(request.getPhone()), UserInfoDO::getContactPhone, request.getPhone())
                .like(Strings.isNotBlank(request.getEmail()), UserInfoDO::getContactEmail, request.getEmail());
        return DataQuery.of(userInfoDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(UserInfoVO.class);
    }

    @Override
    public UserInfoVO userDetail(UserInfoRequest request) {
        Long id = Objects1.def(request.getId(), Currents::getUserId);
        UserInfoDO info = userInfoDAO.selectById(id);
        Valid.notNull(info, MessageConst.UNKNOWN_USER);
        UserInfoVO user = Converts.to(info, UserInfoVO.class);
        user.setAvatar(AvatarPicHolder.getUserAvatar(info.getAvatarPic()));
        return user;
    }

    @Override
    public Long addUser(UserInfoRequest request) {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername());
        UserInfoDO query = userInfoDAO.selectOne(wrapper);
        if (query != null) {
            throw Exceptions.httpWrapper(HttpWrapper.error(MessageConst.USERNAME_PRESENT));
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
        insert.setRoleType(request.getRole());
        insert.setUserStatus(Const.ENABLE);
        insert.setContactPhone(request.getPhone());
        insert.setContactEmail(request.getEmail());
        userInfoDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateUser(UserInfoRequest request) {
        Long userId = Currents.getUserId();
        Long updateId = request.getId();
        String nickname = request.getNickname();
        final boolean updateCurrent = updateId.equals(userId);
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(updateId);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 设置更新信息
        UserInfoDO update = new UserInfoDO();
        update.setId(updateId);
        update.setUsername(userInfo.getUsername());
        update.setNickname(nickname);
        update.setContactPhone(request.getPhone());
        update.setContactEmail(request.getEmail());
        update.setUserStatus(request.getStatus());
        update.setUpdateTime(new Date());
        if (!updateCurrent && Currents.isAdministrator()) {
            update.setRoleType(request.getRole());
        }
        int effect = userInfoDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        // 更新用户缓存数据
        this.updateUserCache(updateId, nickname, update.getRoleType(), null);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteUser(Long id) {
        Long userId = Currents.getUserId();
        // 删除检查
        Valid.isTrue(!userId.equals(id), MessageConst.UNSUPPORTED_OPERATOR);
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(id);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 删除用户
        int effect = userInfoDAO.deleteById(id);
        // 删除报警组员
        effect += alarmGroupUserService.deleteByUserId(id);
        // 删除token
        RedisUtils.deleteLoginToken(redisTemplate, id);
        // 删除活跃时间
        userActiveInterceptor.deleteActiveTime(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.USERNAME, userInfo.getUsername());
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(Long id, Integer status) {
        Long userId = Currents.getUserId();
        // 更新检查
        Valid.isTrue(!userId.equals(id), MessageConst.UNSUPPORTED_OPERATOR);
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(id);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 更新
        boolean enable = Const.ENABLE.equals(status);
        UserInfoDO update = new UserInfoDO();
        update.setId(id);
        update.setUserStatus(status);
        update.setUpdateTime(new Date());
        int effect = userInfoDAO.updateById(update);
        // 更新用户缓存数据
        this.updateUserCache(id, null, null, status);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.USERNAME, userInfo.getUsername());
        EventParamsHolder.addParam(EventKeys.OPERATOR, enable ? CnConst.ENABLE : CnConst.DISABLE);
        return effect;
    }

    @Override
    public Integer unlockUser(Long id) {
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(id);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 更新
        UserInfoDO update = new UserInfoDO();
        update.setId(id);
        update.setLockStatus(Const.ENABLE);
        update.setFailedLoginCount(0);
        update.setUpdateTime(new Date());
        int effect = userInfoDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.USERNAME, userInfo.getUsername());
        return effect;
    }

    @Override
    public Integer updateAvatar(String avatar) {
        Long userId = Currents.getUserId();
        UserInfoDO userInfo = userInfoDAO.selectById(userId);
        // 删除原图片
        if (!Strings.isBlank(userInfo.getAvatarPic())) {
            AvatarPicHolder.deletePic(userInfo.getAvatarPic());
        }
        // 写入图片
        String type = Base64s.img64Type(avatar);
        String url = AvatarPicHolder.getPicPath(userId, type);
        byte[] pic = Base64s.img64Decode(avatar);
        String fullPicPath = AvatarPicHolder.touchPicFile(userId, type);
        FileWriters.writeFast(fullPicPath, pic);
        // 更新
        UserInfoDO update = new UserInfoDO();
        update.setId(userId);
        update.setAvatarPic(url);
        update.setUpdateTime(new Date());
        return userInfoDAO.updateById(update);
    }

    @Override
    public void generatorDefaultAdminUser() {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, DEFAULT_USERNAME);
        UserInfoDO admin = userInfoDAO.selectOne(wrapper);
        if (admin != null) {
            return;
        }
        // 密码
        String salt = UUIds.random19();
        String password = ValueMix.encPassword(Signatures.md5(DEFAULT_USERNAME), salt);
        // 创建用户
        UserInfoDO insert = new UserInfoDO();
        insert.setUsername(DEFAULT_USERNAME);
        insert.setNickname(DEFAULT_NICKNAME);
        insert.setPassword(password);
        insert.setSalt(salt);
        insert.setRoleType(RoleType.ADMINISTRATOR.getType());
        insert.setUserStatus(Const.ENABLE);
        userInfoDAO.insert(insert);
    }

    @Override
    public void resetDefaultAdminUserPassword() {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, DEFAULT_USERNAME);
        UserInfoDO admin = userInfoDAO.selectOne(wrapper);
        if (admin != null) {
            // 重置用户
            Long id = admin.getId();
            UserInfoDO update = new UserInfoDO();
            String password = ValueMix.encPassword(Signatures.md5(DEFAULT_USERNAME), admin.getSalt());
            update.setId(id);
            update.setPassword(password);
            update.setFailedLoginCount(0);
            update.setUserStatus(Const.ENABLE);
            update.setLockStatus(Const.ENABLE);
            update.setUpdateTime(new Date());
            userInfoDAO.updateById(update);
            if (!password.equals(admin.getPassword())) {
                // 密码不同删除token
                RedisUtils.deleteLoginToken(redisTemplate, id);
            }
        } else {
            // 生成用户
            this.generatorDefaultAdminUser();
        }
    }

    /**
     * 更新用户缓存
     *
     * @param updateId   updateId
     * @param nickname   nickname
     * @param roleType   roleType
     * @param userStatus userStatus
     */
    private void updateUserCache(Long updateId, String nickname, Integer roleType, Integer userStatus) {
        // 查询缓存
        String cacheKey = Strings.format(KeyConst.LOGIN_TOKEN_KEY, updateId);
        String tokenValue = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isEmpty(tokenValue)) {
            return;
        }
        // 查询过期时间
        Long expire = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        if (expire == null) {
            expire = TimeUnit.HOURS.toSeconds(Long.parseLong(SystemEnvAttr.LOGIN_TOKEN_EXPIRE.getValue()));
        }
        // 更新缓存
        UserDTO userDTO = JSON.parseObject(tokenValue, UserDTO.class);
        if (roleType != null) {
            userDTO.setRoleType(roleType);
        }
        if (!Strings.isBlank(nickname)) {
            userDTO.setNickname(nickname);
        }
        if (userStatus != null) {
            userDTO.setUserStatus(userStatus);
        }
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(userDTO), expire, TimeUnit.SECONDS);
    }

}
