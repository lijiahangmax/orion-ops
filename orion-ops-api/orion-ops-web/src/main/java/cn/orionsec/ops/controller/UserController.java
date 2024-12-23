/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RequireRole;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.request.user.UserInfoRequest;
import cn.orionsec.ops.entity.vo.user.UserInfoVO;
import cn.orionsec.ops.service.api.UserService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.Valid;
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

    @DemoDisableApi
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

    @DemoDisableApi
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

    @DemoDisableApi
    @PostMapping("/update-avatar")
    @ApiOperation(value = "修改用户头像")
    public Integer updateAvatar(@RequestBody UserInfoRequest request) {
        String avatar = Valid.notBlank(request.getAvatar());
        return userService.updateAvatar(avatar);
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DELETE_USER)
    public Integer deleteUser(@RequestBody UserInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return userService.deleteUser(id);
    }

    @DemoDisableApi
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
