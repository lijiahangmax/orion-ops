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
 * 上线单
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("release_bill")
public class ReleaseBillDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布标题
     */
    @TableField("release_title")
    private String releaseTitle;

    /**
     * 发布描述
     */
    @TableField("release_description")
    private String releaseDescription;

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
     * 应用tag
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
     * 环境tag
     */
    @TableField("profile_tag")
    private String profileTag;

    /**
     * 宿主机日志路径
     */
    @TableField("log_path")
    private String logPath;

    /**
     * 发布类型 10正常发布 20回滚发布
     *
     * @see com.orion.ops.consts.app.ReleaseType
     */
    @TableField("release_type")
    private Integer releaseType;

    /**
     * 发布状态 10待审核 20待发布 30发布中 40发布完成 50发布异常
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @TableField("release_status")
    private Integer releaseStatus;

    /**
     * 回滚发布id
     */
    @TableField("rollback_release_id")
    private Long rollbackReleaseId;

    /**
     * 版本控制本地目录
     */
    @TableField("vcs_local_path")
    private String vcsLocalPath;

    /**
     * 版本控制远程url
     */
    @TableField("vcs_remote_url")
    private String vcsRemoteUrl;

    /**
     * 发布分支
     */
    @TableField("branch_name")
    private String branchName;

    /**
     * 发布提交id
     */
    @TableField("commit_id")
    private String commitId;

    /**
     * 发布提交message
     */
    @TableField("commit_message")
    private String commitMessage;

    /**
     * 产物目录
     */
    @TableField("dist_path")
    private String distPath;

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
     * 审核人id
     */
    @TableField("audit_user_id")
    private Long auditUserId;

    /**
     * 审核人名称
     */
    @TableField("audit_user_name")
    private String auditUserName;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private Date auditTime;

    /**
     * 审核备注
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 发布开始时间
     */
    @TableField("release_start_time")
    private Date releaseStartTime;

    /**
     * 发布结束时间
     */
    @TableField("release_end_time")
    private Date releaseEndTime;

    /**
     * 发布人id
     */
    @TableField("release_user_id")
    private Long releaseUserId;

    /**
     * 发布人名称
     */
    @TableField("release_user_name")
    private String releaseUserName;

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
