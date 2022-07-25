package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 22:16
 */
@Data
@ApiModel(value = "用户登录响应")
public class UserLoginVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    /**
     * @see com.orion.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型")
    private Integer roleType;

}
