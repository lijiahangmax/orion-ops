package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.UserInfoRequest;
import com.orion.ops.entity.vo.UserInfoVO;

/**
 * 用户服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:50
 */
public interface UserService {

    /**
     * 查询用户列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<UserInfoVO> userList(UserInfoRequest request);

    /**
     * 查询用户详情
     *
     * @param request request
     * @return row
     */
    UserInfoVO userDetail(UserInfoRequest request);

    /**
     * 添加用户
     *
     * @param request request
     * @return userId
     */
    Long addUser(UserInfoRequest request);

    /**
     * 更新用户
     *
     * @param request request
     * @return effect
     */
    Integer updateUser(UserInfoRequest request);

    /**
     * 删除用户
     *
     * @param id id
     * @return effect
     */
    Integer deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id     id
     * @param status status
     * @return effect
     */
    Integer updateStatus(Long id, Integer status);

    /**
     * 更新头像
     *
     * @param avatar base64
     * @return effect
     */
    Integer updateAvatar(String avatar);

}
