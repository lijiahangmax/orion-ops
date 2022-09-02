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
 * 机器报警配置
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_alarm_config")
@ApiModel(value = "MachineAlarmConfigDO对象", description = "机器报警配置")
@SuppressWarnings("ALL")
public class MachineAlarmConfigDO implements Serializable {

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

    @ApiModelProperty(value = "报警阈值")
    @TableField("alarm_threshold")
    private Double alarmThreshold;

    @ApiModelProperty(value = "触发报警阈值 次")
    @TableField("trigger_threshold")
    private Integer triggerThreshold;

    @ApiModelProperty(value = "报警通知沉默时间 分")
    @TableField("notify_silence")
    private Integer notifySilence;

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
