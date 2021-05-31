package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
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
    HttpWrapper<Long> addUser(UserInfoRequest request);

    /**
     * 更新用户
     *
     * @param request request
     * @return effect
     */
    HttpWrapper<Integer> updateUser(UserInfoRequest request);

    /**
     * 删除用户
     *
     * @param request request
     * @return effect
     */
    HttpWrapper<Integer> deleteUser(UserInfoRequest request);

    /**
     * 更新用户状态
     *
     * @param request request
     * @return effect
     */
    HttpWrapper<Integer> updateStatus(UserInfoRequest request);

    /**
     * 更新头像
     *
     * @param headPic base64
     * @return effect
     */
    Integer updateHeadPic(String headPic);

}
