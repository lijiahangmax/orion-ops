package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器信息请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器信息请求")
public class MachineInfoRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "排除的id")
    private Long excludeId;

    @ApiModelProperty(value = "idList")
    private List<Long> idList;

    @ApiModelProperty(value = "代理id")
    private Long proxyId;

    @ApiModelProperty(value = "主机ip")
    private String host;

    @ApiModelProperty(value = "ssh端口")
    private Integer sshPort;

    @ApiModelProperty(value = "机器名称")
    private String name;

    @ApiModelProperty(value = "机器唯一标识")
    private String tag;

    @ApiModelProperty(value = "机器描述")
    private String description;

    @ApiModelProperty(value = "机器账号")
    private String username;

    @ApiModelProperty(value = "机器密码")
    private String password;

    /**
     * @see com.orion.ops.consts.machine.MachineAuthType
     */
    @ApiModelProperty(value = "机器认证方式 1: 账号认证 2: key认证")
    private Integer authType;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer status;

    @ApiModelProperty(value = "同步属性")
    private String syncProp;

    @ApiModelProperty(value = "跳过宿主机")
    private Integer skipHost;

}
