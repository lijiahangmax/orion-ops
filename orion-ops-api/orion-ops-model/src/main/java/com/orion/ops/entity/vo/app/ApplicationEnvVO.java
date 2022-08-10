package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用环境变量响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:25
 */
@Data
@ApiModel(value = "应用环境变量响应")
@SuppressWarnings("ALL")
public class ApplicationEnvVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    /**
     * @see com.orion.ops.constant.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.constant.Const#FORBID_DELETE_NOT
     */
    @ApiModelProperty(value = "是否禁止删除 1可以删除 2禁止删除")
    private Integer forbidDelete;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
