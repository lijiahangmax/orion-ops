package com.orion.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统环境变量响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:36
 */
@Data
@ApiModel(value = "系统环境变量响应")
@SuppressWarnings("ALL")
public class SystemEnvVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * @see com.orion.ops.constant.Const#FORBID_DELETE_CAN
     * @see com.orion.ops.constant.Const#FORBID_DELETE_NOT
     */
    @ApiModelProperty(value = "是否禁止删除 1可以删除 2禁止删除")
    private Integer forbidDelete;

}
