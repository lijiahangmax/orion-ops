package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 终端日志请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "终端日志请求")
public class MachineTerminalLogRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Integer machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "token")
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间-区间开始")
    private Date connectedTimeStart;

    @ApiModelProperty(value = "建立连接时间-区间结束")
    private Date connectedTimeEnd;

    @ApiModelProperty(value = "断开连接时间-区间开始")
    private Date disconnectedTimeStart;

    @ApiModelProperty(value = "断开连接时间-区间结束")
    private Date disconnectedTimeEnd;

    @ApiModelProperty(value = "close code")
    private Integer closeCode;

}
