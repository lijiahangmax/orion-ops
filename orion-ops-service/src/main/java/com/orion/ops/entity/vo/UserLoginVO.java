package com.orion.ops.entity.vo;

import com.orion.ops.consts.user.RoleType;
import lombok.Data;

/**
 * 用户登录返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 22:16
 */
@Data
public class UserLoginVO {

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 角色类型
     *
     * @see RoleType
     */
    private Integer roleType;

}
