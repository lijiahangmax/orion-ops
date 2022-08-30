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
 * 机器报警历史
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_alarm_history")
@ApiModel(value = "MachineAlarmHistoryDO对象", description = "机器报警历史")
@SuppressWarnings("ALL")
public class MachineAlarmHistoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.machine.MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    @TableField("alarm_type")
    private Integer alarmType;

    @ApiModelProperty(value = "报警值")
    @TableField("alarm_value")
    private Double alarmValue;

    @ApiModelProperty(value = "报警时间")
    @TableField("alarm_time")
    private Date alarmTime;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
