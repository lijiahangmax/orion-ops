package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器代理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器代理请求")
public class MachineProxyRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * @see com.orion.ops.consts.machine.ProxyType
     */
    @ApiModelProperty(value = "代理类型")
    private Integer type;

    @ApiModelProperty(value = "描述")
    private String description;

}
