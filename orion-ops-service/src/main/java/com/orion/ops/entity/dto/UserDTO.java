package com.orion.ops.entity.dto;

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
     * 角色类型
     * <p>
     * {@link com.orion.ops.consts.RoleType}
     */
    private Integer roleType;

}
