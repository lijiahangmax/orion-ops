package com.orion.ops.entity.domain;

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
     * @see com.orion.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "类型")
    @TableField("action_type")
    private Integer actionType;

    /**
     * @see com.orion.ops.constant.app.StageType
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
