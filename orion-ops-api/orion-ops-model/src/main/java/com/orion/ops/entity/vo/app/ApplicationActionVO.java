package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用操作响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:23
 */
@Data
@ApiModel(value = "应用操作响应")
@SuppressWarnings("ALL")
public class ApplicationActionVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @see com.orion.ops.constant.app.ActionType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "命令")
    private String command;

}
