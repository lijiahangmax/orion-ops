package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.lang.wrapper.RpcWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.RoleType;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.service.api.UserService;
import com.orion.ops.utils.Valid;
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
     * 添加用户
     */
    @RequestMapping("/add")
    @RequireRole(value = {RoleType.SUPER_ADMINISTRATOR, RoleType.ADMINISTRATOR})
    public HttpWrapper<Long> addUser(@RequestBody UserInfoRequest request) {
        this.check(request);
        Valid.notBlank(request.getPassword());
        request.setId(null);
        RpcWrapper<Long> res = userService.addUser(request);
        if (res.isSuccess()) {
            return HttpWrapper.ok(res.getData());
        } else {
            return HttpWrapper.error(res.getMsg());
        }
    }

    /**
     * 修改信息
     */
    @RequestMapping("/update")
    public HttpWrapper<Integer> update(@RequestBody UserInfoRequest request) {
        Integer roleType = request.getRoleType();
        if (roleType != null) {
            Valid.notNull(RoleType.of(roleType));
        }

        RpcWrapper<Integer> res = userService.updateUser(request);
        if (res.isSuccess()) {
            return HttpWrapper.ok(res.getData());
        } else {
            return HttpWrapper.error(res.getMsg());
        }
    }

    /**
     * 修改头像
     */
    @RequestMapping("/update/head-pic")
    public Integer updateHeadPic(@RequestBody UserInfoRequest request) {
        String headPic = Valid.notBlank(request.getHeadPic());
        return userService.updateHeadPic(headPic);
    }

    // 详情
    // 列表
    // 删除
    // 批量删除
    // 批量停用

    /**
     * 检查参数
     */
    private void check(UserInfoRequest request) {
        Valid.notBlank(request.getUsername());
        Valid.notBlank(request.getNickname());
        Valid.notNull(RoleType.of(request.getRoleType()));
        Valid.notBlank(request.getContactPhone());
    }

}
