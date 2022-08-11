package com.orion.ops.entity.vo.exec;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 命令执行状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 18:03
 */
@Data
@ApiModel(value = "命令执行状态响应")
public class CommandExecStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "exitCode")
    private Integer exitCode;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

}
