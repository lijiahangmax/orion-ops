package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 发布单
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_release")
public class ApplicationReleaseDO implements Serializable {

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
     * 构建id
     */
    @TableField("build_id")
    private Long buildId;

    /**
     * 构建seq
     */
    @TableField("build_seq")
    private Integer buildSeq;

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
     * 发布类型 10正常发布 20回滚发布
     *
     * @see com.orion.ops.consts.app.ReleaseType
     */
    @TableField("release_type")
    private Integer releaseType;

    /**
     * 发布状态 10待审核 20审核驳回 30待发布 40发布中 50发布完成 60发布停止 70初始化失败 80发布失败
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @TableField("release_status")
    private Integer releaseStatus;

    /**
     * 发布序列 10串行 20并行
     *
     * @see com.orion.ops.consts.app.ReleaseSerialType
     */
    @TableField("release_serialize")
    private Integer releaseSerialize;

    /**
     * 构建产物文件
     */
    @TableField("bundle_path")
    private String bundlePath;

    /**
     * 产物传输路径
     */
    @TableField("transfer_path")
    private String transferPath;

    /**
     * 回滚发布id
     */
    @TableField("rollback_release_id")
    private Long rollbackReleaseId;

    /**
     * 是否是定时发布 10普通发布 20定时发布
     *
     * @see com.orion.ops.consts.app.TimedReleaseType
     */
    @TableField("timed_release")
    private Integer timedRelease;

    /**
     * 定时发布时间
     */
    @TableField("timed_release_time")
    private Date timedReleaseTime;

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
     * 发布操作json
     */
    @TableField("action_config")
    private String actionConfig;

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
