package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用构建
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_build")
public class ApplicationBuildDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用id
     */
    @TableField("app_id")
    private Long appId;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用唯一标识
     */
    @TableField("app_tag")
    private String appTag;

    /**
     * 环境id
     */
    @TableField("profile_id")
    private Long profileId;

    /**
     * 环境名称
     */
    @TableField("profile_name")
    private String profileName;

    /**
     * 环境唯一标识
     */
    @TableField("profile_tag")
    private String profileTag;

    /**
     * 构建序列
     */
    @TableField("build_seq")
    private Integer buildSeq;

    /**
     * 构建分支
     */
    @TableField("branch_name")
    private String branchName;

    /**
     * 构建提交id
     */
    @TableField("commit_id")
    private String commitId;

    /**
     * 应用版本仓库id
     */
    @TableField("vcs_id")
    private Long vcsId;

    /**
     * 构建日志路径
     */
    @TableField("log_path")
    private String logPath;

    /**
     * 构建产物文件
     */
    @TableField("bundle_path")
    private String bundlePath;

    /**
     * 状态 10未开始 20执行中 30已完成 40执行失败 50已取消
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    @TableField("build_status")
    private Integer buildStatus;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建人id
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 构建开始时间
     */
    @TableField("build_start_time")
    private Date buildStartTime;

    /**
     * 构建结束时间
     */
    @TableField("build_end_time")
    private Date buildEndTime;

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
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
