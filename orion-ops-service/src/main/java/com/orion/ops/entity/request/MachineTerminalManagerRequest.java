package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 终端管理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "终端管理请求")
public class MachineTerminalManagerRequest extends PageRequest {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "连接时间-区间开始")
    private Date connectedTimeStart;

    @ApiModelProperty(value = "连接时间-区间结束")
    private Date connectedTimeEnd;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

}
