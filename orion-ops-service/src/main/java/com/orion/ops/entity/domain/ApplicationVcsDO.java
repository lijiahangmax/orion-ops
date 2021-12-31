package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用版本控制
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_vcs")
public class ApplicationVcsDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("vcs_name")
    private String vcsName;

    /**
     * 描述
     */
    @TableField("vcs_description")
    private String vcsDescription;

    /**
     * 类型 1git
     *
     * @see com.orion.ops.consts.app.VcsType
     */
    @TableField("vcs_type")
    private Integer vcsType;

    /**
     * url
     */
    @TableField("vsc_url")
    private String vscUrl;

    /**
     * 用户名
     */
    @TableField("vsc_username")
    private String vscUsername;

    /**
     * 密码
     */
    @TableField("vcs_password")
    private String vcsPassword;

    /**
     * token
     */
    @TableField("vcs_access_token")
    private String vcsAccessToken;

    /**
     * 状态 10未初始化 20初始化中 30正常 40失败
     *
     * @see com.orion.ops.consts.app.VcsStatus
     */
    @TableField("vcs_status")
    private Integer vcsStatus;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
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
