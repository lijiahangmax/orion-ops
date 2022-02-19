package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.utils.Objects1;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/25 18:41
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户列表
     */
    @RequestMapping("/list")
    public DataGrid<UserInfoVO> list(@RequestBody UserInfoRequest request) {
        return userService.userList(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public UserInfoVO detail(@RequestBody UserInfoRequest request) {
        return userService.userDetail(request);
    }

    /**
     * 添加用户
     */
    @RequestMapping("/add")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.ADD_USER)
    public Long addUser(@RequestBody UserInfoRequest request) {
        this.check(request);
        Valid.notBlank(request.getPassword());
        request.setId(null);
        return userService.addUser(request);
    }

    /**
     * 修改信息
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_USER)
    public Integer update(@RequestBody UserInfoRequest request) {
        Integer roleType = request.getRole();
        if (roleType != null) {
            Valid.notNull(RoleType.of(roleType));
        }
        request.setId(Objects1.def(request.getId(), Currents::getUserId));
        return userService.updateUser(request);
    }

    /**
     * 修改头像
     */
    @RequestMapping("/update/avatar")
    public Integer updateAvatar(@RequestBody UserInfoRequest request) {
        String avatar = Valid.notBlank(request.getAvatar());
        return userService.updateAvatar(avatar);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DELETE_USER)
    public Integer deleteUser(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return userService.deleteUser(id);
    }

    /**
     * 停用/启用
     */
    @RequestMapping("/status")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.CHANGE_USER_STATUS)
    public Integer updateUserStatus(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer status = Valid.in(Valid.notNull(request.getStatus()), Const.ENABLE, Const.DISABLE);
        return userService.updateStatus(id, status);
    }

    /**
     * 解锁
     */
    @RequestMapping("/unlock")
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
