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
package cn.orionsec.ops.entity.request.user;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/25 18:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户请求")
@SuppressWarnings("ALL")
public class UserInfoRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * @see cn.orionsec.ops.constant.user.RoleType
     */
    @ApiModelProperty(value = "角色类型 10管理员 20开发 30运维")
    private Integer role;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "用户状态 1启用 2禁用")
    private Integer status;

    @ApiModelProperty(value = "联系手机")
    private String phone;

    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "头像base64")
    private String avatar;

}
