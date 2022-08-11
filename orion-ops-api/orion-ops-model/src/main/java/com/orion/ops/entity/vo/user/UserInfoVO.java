package com.orion.ops.entity.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/5/28 17:03
 */
@Data
@ApiModel(value = "用户信息响应")
@SuppressWarnings("ALL")
public class UserInfoVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "名称")
    private String nickname;

    /**
     * @see com.orion.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型 10管理员 20开发 30运维")
    private Integer role;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "用户状态 1启用 2停用")
    private Integer status;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "锁定状态 1正常 2锁定")
    private Integer locked;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "最后一次登陆时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "最后登录时间")
    private String lastLoginAgo;

    @ApiModelProperty(value = "头像base64")
    private String avatar;

}
