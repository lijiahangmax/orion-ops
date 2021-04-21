package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 终端
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 22:14
 */
@Data
public class MachineTerminalRequest {

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

}
