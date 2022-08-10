package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统环境变量
 *
 * @author Jiahang Li
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "系统环境变量")
@TableName("system_env")
@SuppressWarnings("ALL")
public class SystemEnvDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "key")
    @TableField("attr_key")
    private String attrKey;

    @ApiModelProperty(value = "value")
    @TableField("attr_value")
    private String attrValue;

    /**
     * @see com.orion.ops.constant.Const#IS_SYSTEM
     * @see com.orion.ops.constant.Const#NOT_SYSTEM
     */
    @ApiModelProperty(value = "是否为系统变量 1是 2否")
    @TableField("system_env")
    private Integer systemEnv;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

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
