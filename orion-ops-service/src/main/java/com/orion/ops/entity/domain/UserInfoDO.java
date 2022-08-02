package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author Jiahang Li
 * @since 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户表")
@TableName("user_info")
public class UserInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "盐值")
    @TableField("salt")
    private String salt;

    /**
     * @see com.orion.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型 10管理员 20开发 30运维")
    @TableField("role_type")
    private Integer roleType;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "用户状态 1启用 2禁用")
    @TableField("user_status")
    private Integer userStatus;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "锁定状态 1正常 2锁定")
    @TableField("lock_status")
    private Integer lockStatus;

    @ApiModelProperty(value = "登陆失败次数")
    @TableField("failed_login_count")
    private Integer failedLoginCount;

    @ApiModelProperty(value = "头像地址")
    @TableField("avatar_pic")
    private String avatarPic;

    @ApiModelProperty(value = "联系手机")
    @TableField("contact_phone")
    private String contactPhone;

    @ApiModelProperty(value = "联系邮箱")
    @TableField("contact_email")
    private String contactEmail;

    @ApiModelProperty(value = "最后登录时间")
    @TableField("last_login_time")
    private Date lastLoginTime;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
