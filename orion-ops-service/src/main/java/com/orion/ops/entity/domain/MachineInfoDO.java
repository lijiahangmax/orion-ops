package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    @TableField("proxy_id")
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
     */
    @TableField("auth_type")
    private Integer authType;

    /**
     * 系统类型  1: linux 2: windows
     */
    @TableField("system_type")
    private Integer systemType;

    /**
     * 机器版本 如: centOS7.0
     */
    @TableField("system_version")
    private String systemVersion;

    /**
     * 机器状态 1有效 2无效
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
