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
package cn.orionsec.ops.entity.request.app;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用版本仓库请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 18:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用版本仓库请求")
@SuppressWarnings("ALL")
public class ApplicationRepositoryRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see cn.orionsec.ops.constant.app.RepositoryType
     */
    @ApiModelProperty(value = "类型 1git")
    private Integer type;

    /**
     * @see cn.orionsec.ops.constant.app.RepositoryStatus
     */
    @ApiModelProperty(value = "状态 10未初始化 20初始化中 30正常 40失败")
    private Integer status;

    /**
     * @see cn.orionsec.ops.constant.app.RepositoryAuthType
     */
    @ApiModelProperty(value = "认证类型 10密码 20令牌")
    private Integer authType;

    /**
     * @see cn.orionsec.ops.constant.app.RepositoryTokenType
     */
    @ApiModelProperty(value = "令牌类型 10github 20gitee 30gitlab")
    private Integer tokenType;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "令牌")
    private String privateToken;

    @ApiModelProperty(value = "分支名称")
    private String branchName;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

}
