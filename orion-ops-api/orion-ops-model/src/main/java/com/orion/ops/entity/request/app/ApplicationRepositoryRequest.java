package com.orion.ops.entity.request.app;

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
     * @see com.orion.ops.constant.app.RepositoryType
     */
    @ApiModelProperty(value = "类型 1git")
    private Integer type;

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
