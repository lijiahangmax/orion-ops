package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 访问终端 VO
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:40
 */
@Data
public class TerminalAccessVO {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * username
     */
    private String username;

    /**
     * 终端类型
     *
     * @see com.orion.remote.TerminalType
     */
    private String terminalType;

    /**
     * 背景色
     */
    private String backgroundColor;

    /**
     * 字体颜色
     */
    private String fontColor;

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 访问token
     */
    private String accessToken;

}
