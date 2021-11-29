package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 状态 10未初始化 20正常 30失败
     *
     * @see com.orion.ops.consts.app.VcsStatus
     */
    @TableField("vcs_status")
    private Integer vcsStatus;

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
