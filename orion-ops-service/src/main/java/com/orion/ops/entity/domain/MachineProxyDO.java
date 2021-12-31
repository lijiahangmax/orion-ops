package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器代理
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_proxy")
public class MachineProxyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理主机
     */
    @TableField("proxy_host")
    private String proxyHost;

    /**
     * 代理端口
     */
    @TableField("proxy_port")
    private Integer proxyPort;

    /**
     * 代理用户名
     */
    @TableField("proxy_username")
    private String proxyUsername;

    /**
     * 代理密码
     */
    @TableField("proxy_password")
    private String proxyPassword;

    /**
     * 代理类型 1http代理 2socket4代理 3socket5代理
     *
     * @see com.orion.ops.consts.machine.ProxyType
     */
    @TableField("proxy_type")
    private Integer proxyType;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
