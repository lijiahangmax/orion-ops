package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器信息表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_info")
public class MachineInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理id
     */
    @TableField(value = "proxy_id", updateStrategy = FieldStrategy.IGNORED)
    private Long proxyId;

    /**
     * 主机ip
     */
    @TableField("machine_host")
    private String machineHost;

    /**
     * ssh端口
     */
    @TableField("ssh_port")
    private Integer sshPort;

    /**
     * 机器名称
     */
    @TableField("machine_name")
    private String machineName;

    /**
     * 机器标签
     */
    @TableField("machine_tag")
    private String machineTag;

    /**
     * 机器描述
     */
    @TableField("description")
    private String description;

    /**
     * 机器账号
     */
    @TableField("username")
    private String username;

    /**
     * 机器密码
     */
    @TableField("password")
    private String password;

    /**
     * 机器认证方式 1: 账号认证 2: key认证
     *
     * @see com.orion.ops.consts.machine.MachineAuthType
     */
    @TableField("auth_type")
    private Integer authType;

    /**
     * 机器状态 1有效 2无效
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @TableField("machine_status")
    private Integer machineStatus;

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
