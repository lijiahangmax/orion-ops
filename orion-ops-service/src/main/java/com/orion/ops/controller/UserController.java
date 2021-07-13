package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Wrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.RoleType;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;
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
    public Wrapper<Long> addUser(@RequestBody UserInfoRequest request) {
        this.check(request);
        Valid.notBlank(request.getPassword());
        request.setId(null);
        return userService.addUser(request);
    }

    /**
     * 修改信息
     */
    @RequestMapping("/update")
    public Wrapper<Integer> update(@RequestBody UserInfoRequest request) {
        Integer roleType = request.getRole();
        if (roleType != null) {
            Valid.notNull(RoleType.of(roleType));
        }
        return userService.updateUser(request);
    }

    /**
     * 修改头像
     */
    @RequestMapping("/update/head-pic")
    public Integer updateHeadPic(@RequestBody UserInfoRequest request) {
        String headPic = Valid.notBlank(request.getHeadPic());
        return userService.updateHeadPic(headPic);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequireRole(RoleType.ADMINISTRATOR)
    public Wrapper<Integer> delete(@RequestBody UserInfoRequest request) {
        Valid.notEmpty(request.getIdList());
        return userService.deleteUser(request);
    }

    /**
     * 停用/启用
     */
    @RequestMapping("/status")
    @RequireRole(RoleType.ADMINISTRATOR)
    public Wrapper<Integer> status(@RequestBody UserInfoRequest request) {
        Valid.notEmpty(request.getIdList());
        Integer status = Valid.notNull(request.getStatus());
        Valid.isTrue(Const.ENABLE.equals(status) || Const.DISABLE.equals(status));
        return userService.updateStatus(request);
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
