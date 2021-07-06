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
 * 应用环境变量
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_env")
public class ApplicationEnvDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用id
     */
    @TableField("app_id")
    private Long appId;

    /**
     * 环境id
     */
    @TableField("profile_id")
    private Long profileId;

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
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

}
