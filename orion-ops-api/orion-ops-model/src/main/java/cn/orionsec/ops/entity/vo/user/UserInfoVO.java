/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.entity.vo.user;

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
     * @see cn.orionsec.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型 10管理员 20开发 30运维")
    private Integer role;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "用户状态 1启用 2停用")
    private Integer status;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "锁定状态 1正常 2锁定")
    private Integer locked;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "最后登录时间")
    private String lastLoginAgo;

    @ApiModelProperty(value = "头像base64")
    private String avatar;

}
