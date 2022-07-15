package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用发布执行块
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_action")
public class ApplicationActionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * appId
     */
    @TableField("app_id")
    private Long appId;

    /**
     * profileId
     */
    @TableField("profile_id")
    private Long profileId;

    /**
     * 名称
     */
    @TableField("action_name")
    private String actionName;

    /**
     * 类型
     *
     * @see com.orion.ops.constant.app.ActionType
     */
    @TableField("action_type")
    private Integer actionType;

    /**
     * 阶段类型
     *
     * @see com.orion.ops.constant.app.StageType
     */
    @TableField("stage_type")
    private Integer stageType;

    /**
     * 执行命令
     */
    @TableField("action_command")
    private String actionCommand;

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
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
