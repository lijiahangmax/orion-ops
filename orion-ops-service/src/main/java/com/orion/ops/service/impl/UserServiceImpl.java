package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;
import com.orion.ops.interceptor.UserActiveInterceptor;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.*;
import com.orion.utils.Exceptions;
import com.orion.utils.Objects1;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.FileWriters;
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

    @Resource
    private UserInfoDAO userInfoDAO;

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
        UserInfoDO user = userInfoDAO.selectById(id);
        Valid.notNull(user, MessageConst.UNKNOWN_USER);
        UserInfoVO userVo = Converts.to(user, UserInfoVO.class);
        if (AvatarPicHolder.isExist(user.getAvatarPic())) {
            userVo.setAvatar(AvatarPicHolder.getBase64(user.getAvatarPic()));
        }
        return userVo;
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
        Long userId = insert.getId();
        // 生成头像
        String avatar = AvatarPicHolder.generatorUserAvatar(userId, request.getNickname());
        UserInfoDO update = new UserInfoDO();
        update.setId(userId);
        update.setAvatarPic(avatar);
        update.setUpdateTime(new Date());
        userInfoDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return userId;
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
    public Integer deleteUser(Long id) {
        Long userId = Currents.getUserId();
        // 删除检查
        Valid.isTrue(!userId.equals(id), MessageConst.UNSUPPORTED_OPERATOR);
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(id);
        Valid.notNull(userInfo, MessageConst.UNKNOWN_USER);
        // 删除
        int effect = userInfoDAO.deleteById(id);
        // 删除token
        RedisUtils.deleteLoginToken(redisTemplate, userId);
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
        EventParamsHolder.addParam(EventKeys.OPERATOR, enable ? Const.ENABLE_LABEL : Const.DISABLE_LABEL);
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
