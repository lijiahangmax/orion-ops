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
 * 机器终端操作日志
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("machine_terminal_log")
public class MachineTerminalLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 机器id
     */
    @TableField("machine_id")
    private Long machineId;

    /**
     * 机器名称
     */
    @TableField("machine_name")
    private String machineName;

    /**
     * 机器唯一标识
     */
    @TableField("machine_tag")
    private String machineTag;

    /**
     * 机器host
     */
    @TableField("machine_host")
    private String machineHost;

    /**
     * token
     */
    @TableField("access_token")
    private String accessToken;

    /**
     * 建立连接时间
     */
    @TableField("connected_time")
    private Date connectedTime;

    /**
     * 断开连接时间
     */
    @TableField("disconnected_time")
    private Date disconnectedTime;

    /**
     * close code
     *
     * @see com.orion.ops.constant.ws.WsCloseCode
     */
    @TableField("close_code")
    private Integer closeCode;

    /**
     * 录屏文件路径
     */
    // TODO
    @TableField("operate_log_file")
    private String screenPath;

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
