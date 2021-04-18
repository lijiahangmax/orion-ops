package com.orion.ops.service.api;

import com.orion.ops.entity.dto.UserDTO;

/**
 * 认证服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:58
 */
public interface PassportService {

    /**
     * 通过token获取用户
     *
     * @param token token
     * @return user
     */
    UserDTO getUserByToken(String token);

}
