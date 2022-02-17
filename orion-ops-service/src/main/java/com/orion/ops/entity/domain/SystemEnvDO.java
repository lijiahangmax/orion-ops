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
 * 系统环境变量
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_env")
public class SystemEnvDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * key
     */
    @TableField("attr_key")
    private String attrKey;

    /**
     * value
     */
    @TableField("attr_value")
    private String attrValue;

    /**
     * 是否为系统变量 1是 2否
     *
     * @see com.orion.ops.consts.Const#IS_SYSTEM
     * @see com.orion.ops.consts.Const#NOT_SYSTEM
     */
    @TableField("system_env")
    private Integer systemEnv;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否删除 1未删除 2已删除
     */
    @TableField("deleted")
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
