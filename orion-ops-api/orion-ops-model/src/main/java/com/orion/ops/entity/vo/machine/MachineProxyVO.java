package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器代理响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 22:01
 */
@Data
@ApiModel(value = "机器代理响应")
public class MachineProxyVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "代理类型")
    private Integer type;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
