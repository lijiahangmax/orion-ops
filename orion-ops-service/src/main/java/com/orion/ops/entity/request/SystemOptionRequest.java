package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 22:23
 */
@Data
@ApiModel(value = "系统配置请求")
public class SystemOptionRequest {

    /**
     * @see com.orion.ops.consts.system.SystemConfigKey
     */
    @ApiModelProperty(value = "配置项")
    private Integer option;

    @ApiModelProperty(value = "配置值")
    private String value;

}
