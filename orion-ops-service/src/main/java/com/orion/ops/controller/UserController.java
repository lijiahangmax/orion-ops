package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Objects1;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/25 18:41
 */
@Api(tags = "用户")
@RestController
@RestWrapper
@RequestMapping("/orion/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/list")
    @ApiOperation(value = "获取用户列表")
    public DataGrid<UserInfoVO> list(@RequestBody UserInfoRequest request) {
        return userService.userList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取用户详情")
    public UserInfoVO detail(@RequestBody UserInfoRequest request) {
        return userService.userDetail(request);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加用户")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.ADD_USER)
    public Long addUser(@RequestBody UserInfoRequest request) {
        this.check(request);
        Valid.notBlank(request.getPassword());
        request.setId(null);
        return userService.addUser(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改用户信息")
    @EventLog(EventType.UPDATE_USER)
    public Integer update(@RequestBody UserInfoRequest request) {
        Integer roleType = request.getRole();
        if (roleType != null) {
            Valid.notNull(RoleType.of(roleType));
        }
        request.setId(Objects1.def(request.getId(), Currents::getUserId));
        return userService.updateUser(request);
    }

    @PostMapping("/update-avatar")
    @ApiOperation(value = "修改用户头像")
    public Integer updateAvatar(@RequestBody UserInfoRequest request) {
        String avatar = Valid.notBlank(request.getAvatar());
        return userService.updateAvatar(avatar);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DELETE_USER)
    public Integer deleteUser(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return userService.deleteUser(id);
    }

    @PostMapping("/update-status")
    @ApiOperation(value = "停用/启用用户")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.CHANGE_USER_STATUS)
    public Integer updateUserStatus(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer status = Valid.in(Valid.notNull(request.getStatus()), Const.ENABLE, Const.DISABLE);
        return userService.updateStatus(id, status);
    }

    @PostMapping("/unlock")
    @ApiOperation(value = "解锁用户")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.UNLOCK_USER)
    public Integer unlockUser(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return userService.unlockUser(id);
    }

    /**
     * 检查参数
     */
    private void check(UserInfoRequest request) {
        Valid.notBlank(request.getUsername());
        Valid.notBlank(request.getNickname());
        Valid.notNull(RoleType.of(request.getRole()));
        Valid.notBlank(request.getPhone());
    }

}
