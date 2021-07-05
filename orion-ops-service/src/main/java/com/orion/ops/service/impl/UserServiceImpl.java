package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.*;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.*;
import com.orion.utils.Objects1;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.FileWriters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    public DataGrid<UserInfoVO> userList(UserInfoRequest request) {
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(Objects.nonNull(request.getId()), UserInfoDO::getId, request.getId())
                .eq(Objects.nonNull(request.getRole()), UserInfoDO::getRoleType, request.getRole())
                .ne(!Currents.isAdministrator(), UserInfoDO::getRoleType, RoleType.ADMINISTRATOR.getType())
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
        Valid.notNull(user, "未查询到用户信息");
        UserInfoVO userVo = Converts.to(user, UserInfoVO.class);
        if (HeadPicHolder.isExist(user.getHeadPic())) {
            userVo.setHeadPic(HeadPicHolder.getBase64(user.getHeadPic()));
        }
        return userVo;
    }

    @Override
    public HttpWrapper<Long> addUser(UserInfoRequest request) {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, request.getUsername());
        UserInfoDO query = userInfoDAO.selectOne(wrapper);
        if (query != null) {
            return HttpWrapper.error("用户名已存在");
        }
        // 角色判断
        RoleType role = RoleType.of(request.getRole());
        if (RoleType.ADMINISTRATOR.equals(role)) {
            return HttpWrapper.error("不支持创建该角色");
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
        String headPic = HeadPicHolder.generatorUserHeadPic(userId, request.getNickname());
        UserInfoDO update = new UserInfoDO();
        update.setId(userId);
        update.setHeadPic(headPic);
        update.setUpdateTime(new Date());
        userInfoDAO.updateById(update);
        return HttpWrapper.ok(userId);
    }

    @Override
    public HttpWrapper<Integer> updateUser(UserInfoRequest request) {
        Long userId = Currents.getUserId();
        Long updateId;
        boolean updateCurrent = true;
        if (request.getId() != null) {
            updateId = request.getId();
            updateCurrent = updateId.equals(userId);
        } else {
            updateId = userId;
        }
        // 查询用户信息
        UserInfoDO userInfo = userInfoDAO.selectById(updateId);
        if (userInfo == null) {
            return HttpWrapper.error("未查询到用户信息");
        }
        UserInfoDO update = new UserInfoDO();
        update.setId(updateId);
        update.setNickname(request.getNickname());
        update.setContactPhone(request.getPhone());
        update.setContactEmail(request.getEmail());
        update.setUserStatus(request.getStatus());
        update.setUpdateTime(new Date());
        RoleType roleType = RoleType.of(request.getRole());
        if (!updateCurrent) {
            if (!Currents.isAdministrator()) {
                return HttpWrapper.of(ResultCode.NO_PERMISSION);
            }
            if (roleType != null) {
                if (RoleType.ADMINISTRATOR.equals(roleType)) {
                    return HttpWrapper.of(ResultCode.NO_PERMISSION);
                }
                update.setRoleType(roleType.getType());
            }
        }
        int effect = userInfoDAO.updateById(update);
        // 更新token
        String cacheKey = Strings.format(KeyConst.LOGIN_TOKEN_KEY, updateId);
        String tokenValue = redisTemplate.opsForValue().get(cacheKey);
        if (Strings.isEmpty(tokenValue)) {
            return HttpWrapper.ok(effect);
        }
        UserDTO userDTO = JSON.parseObject(tokenValue, UserDTO.class);
        Optional.ofNullable(roleType)
                .map(RoleType::getType)
                .ifPresent(userDTO::setRoleType);
        Optional.ofNullable(request.getNickname())
                .filter(Strings::isNotBlank)
                .ifPresent(userDTO::setNickname);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(userDTO), KeyConst.LOGIN_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return HttpWrapper.ok(effect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpWrapper<Integer> deleteUser(UserInfoRequest request) {
        List<Long> idList = Lists.newList();
        HttpWrapper<Integer> check = this.updateOrDeleteCheck(request, idList);
        if (!check.isOk()) {
            return check;
        }
        int effect = 0;
        for (Long id : idList) {
            effect += userInfoDAO.deleteById(id);
            redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, id));
        }
        return HttpWrapper.ok(effect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpWrapper<Integer> updateStatus(UserInfoRequest request) {
        List<Long> idList = Lists.newList();
        HttpWrapper<Integer> check = this.updateOrDeleteCheck(request, idList);
        if (!check.isOk()) {
            return check;
        }
        Integer status = request.getStatus();
        boolean disable = Const.DISABLE.equals(status);
        int effect = 0;
        for (Long id : idList) {
            UserInfoDO update = new UserInfoDO();
            update.setId(id);
            update.setUserStatus(status);
            update.setUpdateTime(new Date());
            effect += userInfoDAO.updateById(update);
            if (disable) {
                redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, id));
            }
        }
        return HttpWrapper.ok(effect);
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
        update.setUpdateTime(new Date());
        return userInfoDAO.updateById(update);
    }

    /**
     * 修改或删除前检查
     */
    private HttpWrapper<Integer> updateOrDeleteCheck(UserInfoRequest request, List<Long> idList) {
        Long userId = Currents.getUserId();
        for (Long id : request.getIdList()) {
            if (id == null) {
                return HttpWrapper.error(MessageConst.MISSING_PARAM);
            }
            if (userId.equals(id)) {
                return HttpWrapper.of(ResultCode.NO_PERMISSION);
            }
            UserInfoDO user = userInfoDAO.selectById(id);
            if (user == null) {
                return HttpWrapper.error("未查询到用户信息");
            }
            RoleType role = RoleType.of(user.getRoleType());
            if (RoleType.ADMINISTRATOR.equals(role)) {
                return HttpWrapper.of(ResultCode.NO_PERMISSION);
            }
            idList.add(id);
        }
        return HttpWrapper.ok();
    }

}
