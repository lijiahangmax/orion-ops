package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:59
 */
@Data
@ApiModel(value = "终端日志响应")
@SuppressWarnings("ALL")
public class MachineTerminalLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "访问token")
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    private Date disconnectedTime;

    @ApiModelProperty(value = "建立连接时间")
    private String connectedTimeAgo;

    @ApiModelProperty(value = "断开连接时间")
    private String disconnectedTimeAgo;

    /**
     * @see com.orion.ops.constant.ws.WsCloseCode
     */
    @ApiModelProperty(value = "close code")
    private Integer closeCode;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
