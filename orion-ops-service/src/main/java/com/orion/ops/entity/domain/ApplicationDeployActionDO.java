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
 * 应用发布执行块
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_deploy_action")
public class ApplicationDeployActionDO implements Serializable {

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
     */
    @TableField("action_type")
    private Integer actionType;

    /**
     * 执行命令
     */
    @TableField("action_command")
    private String actionCommand;

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
