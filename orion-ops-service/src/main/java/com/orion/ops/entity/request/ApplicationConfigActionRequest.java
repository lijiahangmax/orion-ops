package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用发布操作配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/7 19:00
 */
@Data
@ApiModel(value = "应用发布操作配置请求")
public class ApplicationConfigActionRequest {

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @see com.orion.ops.consts.app.ActionType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "执行命令")
    private String command;

}
