package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器终端管理响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:42
 */
@Data
@ApiModel(value = "机器终端管理响应")
public class MachineTerminalManagerVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "username")
    private String userName;

    @ApiModelProperty(value = "连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "连接时间")
    private String connectedTimeAgo;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "logId")
    private Long logId;

}
