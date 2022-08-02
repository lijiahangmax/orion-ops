package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 16:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户信息")
public class UserDTO implements Serializable {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    /**
     * @see com.orion.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型")
    private Integer roleType;

    @ApiModelProperty(value = "登录时间戳")
    private Long timestamp;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "用户状态 1启用 2禁用")
    private Integer userStatus;

    @ApiModelProperty(value = "当前用户绑定时间戳 无需设置")
    private Long currentBindTimestamp;

}
