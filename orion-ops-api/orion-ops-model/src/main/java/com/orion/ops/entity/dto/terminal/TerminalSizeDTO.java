package com.orion.ops.entity.dto.terminal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 终端大小参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 13:58
 */
@Data
@ApiModel(value = "终端大小参数")
public class TerminalSizeDTO {

    @ApiModelProperty(value = "列数")
    private Integer cols;

    @ApiModelProperty(value = "行数")
    private Integer rows;

}
