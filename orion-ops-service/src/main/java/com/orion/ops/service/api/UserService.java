package com.orion.ops.service.api;

import com.orion.lang.wrapper.RpcWrapper;
import com.orion.ops.entity.request.UserInfoRequest;

/**
 * 用户服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:50
 */
public interface UserService {

    /**
     * 添加用户
     *
     * @param request request
     * @return userId
     */
    RpcWrapper<Long> addUser(UserInfoRequest request);

    /**
     * 更新用户
     *
     * @param request request
     * @return effect
     */
    RpcWrapper<Integer> updateUser(UserInfoRequest request);

    /**
     * 更新头像
     *
     * @param headPic base64
     * @return effect
     */
    Integer updateHeadPic(String headPic);

}
