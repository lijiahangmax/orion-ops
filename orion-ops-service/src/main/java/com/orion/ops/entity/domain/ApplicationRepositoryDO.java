package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用版本仓库
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_repository")
public class ApplicationRepositoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("repo_name")
    private String repoName;

    /**
     * 描述
     */
    @TableField("repo_description")
    private String repoDescription;

    /**
     * 类型 1git
     *
     * @see com.orion.ops.constant.app.RepositoryType
     */
    @TableField("repo_type")
    private Integer repoType;

    /**
     * url
     */
    @TableField("repo_url")
    private String repoUrl;

    /**
     * 用户名
     */
    @TableField("repo_username")
    private String repoUsername;

    /**
     * 密码
     */
    @TableField("repo_password")
    private String repoPassword;

    /**
     * token
     */
    @TableField("repo_private_token")
    private String repoPrivateToken;

    /**
     * 状态 10未初始化 20初始化中 30正常 40失败
     *
     * @see com.orion.ops.constant.app.RepositoryStatus
     */
    @TableField("repo_status")
    private Integer repoStatus;

    /**
     * 认证类型 10密码 20令牌
     *
     * @see com.orion.ops.constant.app.RepositoryAuthType
     */
    @TableField("repo_auth_type")
    private Integer repoAuthType;

    /**
     * 令牌类型 10github 20gitee 30gitlab
     *
     * @see com.orion.ops.constant.app.RepositoryTokenType
     */
    @TableField("repo_token_type")
    private Integer repoTokenType;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

}
