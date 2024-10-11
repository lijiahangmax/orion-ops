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
package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用构建
 *
 * @author Jiahang Li
 * @since 2021-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用构建")
@TableName("application_build")
@SuppressWarnings("ALL")
public class ApplicationBuildDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "构建序列")
    @TableField("build_seq")
    private Integer buildSeq;

    @ApiModelProperty(value = "构建分支")
    @TableField("branch_name")
    private String branchName;

    @ApiModelProperty(value = "构建提交id")
    @TableField("commit_id")
    private String commitId;

    @ApiModelProperty(value = "应用版本仓库id")
    @TableField("repo_id")
    private Long repoId;

    @ApiModelProperty(value = "构建日志路径")
    @TableField("log_path")
    private String logPath;

    @ApiModelProperty(value = "构建产物文件")
    @TableField("bundle_path")
    private String bundlePath;

    /**
     * @see com.orion.ops.constant.app.BuildStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20执行中 30已完成 40执行失败 50已取消")
    @TableField("build_status")
    private Integer buildStatus;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "创建人id")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    @TableField("create_user_name")
    private String createUserName;

    @ApiModelProperty(value = "构建开始时间")
    @TableField("build_start_time")
    private Date buildStartTime;

    @ApiModelProperty(value = "构建结束时间")
    @TableField("build_end_time")
    private Date buildEndTime;

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
