package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器代理
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器代理")
@TableName("machine_proxy")
public class MachineProxyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "代理主机")
    @TableField("proxy_host")
    private String proxyHost;

    @ApiModelProperty(value = "代理端口")
    @TableField("proxy_port")
    private Integer proxyPort;

    @ApiModelProperty(value = "代理用户名")
    @TableField("proxy_username")
    private String proxyUsername;

    @ApiModelProperty(value = "代理密码")
    @TableField("proxy_password")
    private String proxyPassword;

    /**
     * @see com.orion.ops.constant.machine.ProxyType
     */
    @ApiModelProperty(value = "代理类型 1http代理 2socket4代理 3socket5代理")
    @TableField("proxy_type")
    private Integer proxyType;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
