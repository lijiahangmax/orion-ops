package com.orion.ops.entity.vo.exec;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 命令提交响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/18 22:25
 */
@Data
@ApiModel(value = "命令提交响应")
public class CommandTaskSubmitVO {

    @ApiModelProperty(value = "执行id")
    private Long execId;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

}
