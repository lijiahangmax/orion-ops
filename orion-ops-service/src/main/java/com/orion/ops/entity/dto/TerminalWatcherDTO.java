package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终端监视信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 10:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "终端监视信息")
public class TerminalWatcherDTO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "会话token")
    private String token;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否只读")
    private Integer readonly;

}
