package com.orion.ops.entity.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端连接配置项
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:06
 */
@Data
@ApiModel(value = "终端连接配置项")
public class TerminalConnectConfig {

    @ApiModelProperty(value = "行数")
    private Integer rows;

    @ApiModelProperty(value = "列数")
    private Integer cols;

    /**
     * @see com.orion.net.remote.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "日志id")
    private Long logId;

}
