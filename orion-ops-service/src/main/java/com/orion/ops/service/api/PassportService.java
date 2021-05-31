package com.orion.ops.service.api;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.UserLoginRequest;
import com.orion.ops.entity.request.UserResetRequest;
import com.orion.ops.entity.vo.UserLoginVO;

/**
 * 认证服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:58
 */
public interface PassportService {

    /**
     * 登录
     *
     * @param request request
     * @return wrapper
     */
    HttpWrapper<UserLoginVO> login(UserLoginRequest request);

    /**
     * 登出
     */
    void logout();

    /**
     * 重置密码
     *
     * @param request request
     * @return 是否需要删除cookie
     */
    HttpWrapper<Boolean> resetPassword(UserResetRequest request);

    /**
     * 通过token获取用户 所有通过token查用户都要用此方法
     *
     * @param token token
     * @return user
     */
    UserDTO getUserByToken(String token);

}
