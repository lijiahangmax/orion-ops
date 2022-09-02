package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器报警通知组
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_alarm_group")
@ApiModel(value = "MachineAlarmGroupDO对象", description = "机器报警通知组")
public class MachineAlarmGroupDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = " 报警组id")
    @TableField("group_id")
    private Long groupId;

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
