package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 报警组成员
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("alarm_group_user")
@ApiModel(value = "AlarmGroupUserDO对象", description = "报警组成员")
public class AlarmGroupUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "报警组id")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "报警组成员id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "报警组成员用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableField("deleted")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
