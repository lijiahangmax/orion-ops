package com.orion.ops.handler.terminal;

import lombok.Data;

import java.util.Date;

/**
 * 终端连接配置项
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:06
 */
@Data
public class TerminalConnectConfig {

    private String sessionId;

    /**
     * 行数
     */
    private Integer rows;

    /**
     * 行字数
     */
    private Integer cols;

    /**
     * 宽px
     */
    private Integer width;

    /**
     * 高px
     */
    private Integer height;

    /**
     * 终端类型
     */
    private String terminalType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * 连接时间
     */
    private Date connectedTime;

    /**
     * 日志id
     */
    private Long logId;

    public TerminalConnectConfig() {
        this.connectedTime = new Date();
    }

}
