package com.orion.ops.entity.dto.terminal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 终端连接参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 20:12
 */
@Data
@ApiModel(value = "终端连接参数")
public class TerminalConnectDTO {

    @ApiModelProperty(value = "列数")
    private Integer cols;

    @ApiModelProperty(value = "行数")
    private Integer rows;

    @ApiModelProperty(value = "登陆 token")
    private String loginToken;

}
