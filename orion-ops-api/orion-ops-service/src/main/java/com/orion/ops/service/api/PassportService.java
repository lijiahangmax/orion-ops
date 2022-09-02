package com.orion.ops.service.api;

import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.user.UserLoginRequest;
import com.orion.ops.entity.request.user.UserResetRequest;
import com.orion.ops.entity.vo.user.UserLoginVO;

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
    UserLoginVO login(UserLoginRequest request);

    /**
     * 登出
     */
    void logout();

    /**
     * 重置密码
     *
     * @param request request
     */
    void resetPassword(UserResetRequest request);

    /**
     * 通过token获取用户 所有通过token查用户都要用此方法
     *
     * @param token   token
     * @param checkIp 检查的ip 不检查设置为null
     * @return user
     */
    UserDTO getUserByToken(String token, String checkIp);

}
