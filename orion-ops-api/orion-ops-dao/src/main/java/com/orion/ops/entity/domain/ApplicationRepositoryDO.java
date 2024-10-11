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
package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用版本仓库
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用版本仓库")
@TableName("application_repository")
@SuppressWarnings("ALL")
public class ApplicationRepositoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "名称")
    @TableField("repo_name")
    private String repoName;

    @ApiModelProperty(value = "描述")
    @TableField("repo_description")
    private String repoDescription;

    /**
     * @see com.orion.ops.constant.app.RepositoryType
     */
    @ApiModelProperty(value = "类型 1git")
    @TableField("repo_type")
    private Integer repoType;

    @ApiModelProperty(value = "url")
    @TableField("repo_url")
    private String repoUrl;

    @ApiModelProperty(value = "用户名")
    @TableField("repo_username")
    private String repoUsername;

    @ApiModelProperty(value = "密码")
    @TableField("repo_password")
    private String repoPassword;

    @ApiModelProperty(value = "token")
    @TableField("repo_private_token")
    private String repoPrivateToken;

    /**
     * @see com.orion.ops.constant.app.RepositoryStatus
     */
    @ApiModelProperty(value = "状态 10未初始化 20初始化中 30正常 40失败")
    @TableField("repo_status")
    private Integer repoStatus;

    /**
     * @see com.orion.ops.constant.app.RepositoryAuthType
     */
    @ApiModelProperty(value = "认证类型 10密码 20令牌")
    @TableField("repo_auth_type")
    private Integer repoAuthType;

    /**
     * @see com.orion.ops.constant.app.RepositoryTokenType
     */
    @ApiModelProperty(value = "令牌类型 10github 20gitee 30gitlab")
    @TableField("repo_token_type")
    private Integer repoTokenType;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

}
