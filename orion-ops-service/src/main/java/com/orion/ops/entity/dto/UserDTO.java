package com.orion.ops.entity.dto;

import com.orion.ops.consts.user.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * user
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 16:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO implements Serializable {

    /**
     * 用户id
     */
    private Long id;

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

    /**
     * 登录时间戳
     */
    private Long timestamp;

}
