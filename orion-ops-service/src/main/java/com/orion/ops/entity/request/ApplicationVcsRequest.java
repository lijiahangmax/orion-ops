package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app 版本控制请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 18:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationVcsRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * url
     */
    private String url;

    /**
     * name
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型 1git
     *
     * @see com.orion.ops.consts.app.VcsType
     */
    private Integer type;

    /**
     * 状态 10未初始化 20初始化中 30正常 40失败
     *
     * @see com.orion.ops.consts.app.VcsStatus
     */
    private Integer status;

    /**
     * 认证类型 10密码 20令牌
     *
     * @see com.orion.ops.consts.app.VcsAuthType
     */
    private Integer authType;

    /**
     * 令牌类型 10github 20gitee 30gitlab
     *
     * @see com.orion.ops.consts.app.VcsTokenType
     */
    private Integer tokenType;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 令牌
     */
    private String privateToken;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

}
