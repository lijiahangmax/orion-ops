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
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用发布执行块
 *
 * @author Jiahang Li
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用发布执行块")
@TableName("application_action")
@SuppressWarnings("ALL")
public class ApplicationActionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "appId")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "profileId")
    @TableField("profile_id")
    private Long profileId;

    @ApiModelProperty(value = "名称")
    @TableField("action_name")
    private String actionName;

    /**
     * @see cn.orionsec.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "类型")
    @TableField("action_type")
    private Integer actionType;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型")
    @TableField("stage_type")
    private Integer stageType;

    @ApiModelProperty(value = "执行命令")
    @TableField("action_command")
    private String actionCommand;

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
