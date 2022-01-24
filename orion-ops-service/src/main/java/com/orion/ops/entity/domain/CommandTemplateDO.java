package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 命令模板表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("command_template")
public class CommandTemplateDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 命令
     */
    @TableField("template_value")
    private String templateValue;

    /**
     * 命令描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建用户id
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建用户名
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 修改用户id
     */
    @TableField("update_user_id")
    private Long updateUserId;

    /**
     * 修改用户名
     */
    @TableField("update_user_name")
    private String updateUserName;

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
