package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 用户登录
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 23:01
 */
@Data
public class UserLoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
