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
package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用版本仓库信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/28 13:47
 */
@Data
@ApiModel(value = "应用版本仓库信息响应")
@SuppressWarnings("ALL")
public class ApplicationRepositoryVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.app.RepositoryType
     */
    @ApiModelProperty(value = "类型 1git")
    private Integer type;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * @see com.orion.ops.constant.app.RepositoryStatus
     */
    @ApiModelProperty(value = "状态 10未初始化 20初始化中 30正常 40失败")
    private Integer status;

    /**
     * @see com.orion.ops.constant.app.RepositoryAuthType
     */
    @ApiModelProperty(value = "认证类型 10密码 20令牌")
    private Integer authType;

    /**
     * @see com.orion.ops.constant.app.RepositoryTokenType
     */
    @ApiModelProperty(value = "令牌类型 10github 20gitee 30gitlab")
    private Integer tokenType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
