package com.orion.ops.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 终端类型
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