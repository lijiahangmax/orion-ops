package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问监视响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 10:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "访问监视响应")
public class TerminalWatcherVO {

    @ApiModelProperty(value = "token")
    private String token;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否只读")
    private Integer readonly;

    @ApiModelProperty(value = "cols")
    private Integer cols;

    @ApiModelProperty(value = "rows")
    private Integer rows;

}
