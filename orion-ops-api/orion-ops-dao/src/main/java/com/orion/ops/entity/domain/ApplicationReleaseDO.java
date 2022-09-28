package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 发布任务
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "发布任务")
@TableName("application_release")
@SuppressWarnings("ALL")
public class ApplicationReleaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发布标题")
    @TableField("release_title")
    private String releaseTitle;

    @ApiModelProperty(value = "发布描述")
    @TableField("release_description")
    private String releaseDescription;

    @ApiModelProperty(value = "构建id")
    @TableField("build_id")
    private Long buildId;

    @ApiModelProperty(value = "构建seq")
    @TableField("build_seq")
    private Integer buildSeq;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    @TableField("app_name")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    @TableField("app_tag")
    private String appTag;

    @ApiModelProperty(value = "环境id")
    @TableField("profile_id")
    private Long profileId;

    @ApiModelProperty(value = "环境名称")
    @TableField("profile_name")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
    @TableField("profile_tag")
    private String profileTag;

    /**
     * @see com.orion.ops.constant.app.ReleaseType
     */
    @ApiModelProperty(value = "发布类型 10正常发布 20回滚发布")
    @TableField("release_type")
    private Integer releaseType;

    /**
     * @see com.orion.ops.constant.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    @TableField("release_status")
    private Integer releaseStatus;

    /**
     * @see com.orion.ops.constant.common.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    @TableField("release_serialize")
    private Integer releaseSerialize;

    /**
     * @see com.orion.ops.constant.common.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    @TableField("exception_handler")
    private Integer exceptionHandler;

    @ApiModelProperty(value = "构建产物文件")
    @TableField("bundle_path")
    private String bundlePath;

    @ApiModelProperty(value = "产物传输路径")
    @TableField("transfer_path")
    private String transferPath;

    /**
     * @see com.orion.ops.constant.app.TransferMode
     */
    @ApiModelProperty(value = "产物传输方式")
    @TableField("transfer_mode")
    private String transferMode;

    @ApiModelProperty(value = "回滚发布id")
    @TableField("rollback_release_id")
    private Long rollbackReleaseId;

    /**
     * @see com.orion.ops.constant.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时发布 10普通发布 20定时发布")
    @TableField("timed_release")
    private Integer timedRelease;

    @ApiModelProperty(value = "定时发布时间")
    @TableField("timed_release_time")
    private Date timedReleaseTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    @TableField("create_user_name")
    private String createUserName;

    @ApiModelProperty(value = "审核人id")
    @TableField("audit_user_id")
    private Long auditUserId;

    @ApiModelProperty(value = "审核人名称")
    @TableField("audit_user_name")
    private String auditUserName;

    @ApiModelProperty(value = "审核时间")
    @TableField("audit_time")
    private Date auditTime;

    @ApiModelProperty(value = "审核备注")
    @TableField("audit_reason")
    private String auditReason;

    @ApiModelProperty(value = "发布开始时间")
    @TableField("release_start_time")
    private Date releaseStartTime;

    @ApiModelProperty(value = "发布结束时间")
    @TableField("release_end_time")
    private Date releaseEndTime;

    @ApiModelProperty(value = "发布人id")
    @TableField("release_user_id")
    private Long releaseUserId;

    @ApiModelProperty(value = "发布人名称")
    @TableField("release_user_name")
    private String releaseUserName;

    @ApiModelProperty(value = "发布操作json")
    @TableField("action_config")
    private String actionConfig;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
