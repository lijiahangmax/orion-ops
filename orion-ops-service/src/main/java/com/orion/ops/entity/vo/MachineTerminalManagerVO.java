package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * 机器终端 管理员面板
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:42
 */
@Data
public class MachineTerminalManagerVO {

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 连接时间
     */
    private Date connectedTime;

    /**
     * 连接时间
     */
    private String connectedTimeAgo;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 连接主机
     */
    private String host;

    /**
     * logId
     */
    private Long logId;

}
