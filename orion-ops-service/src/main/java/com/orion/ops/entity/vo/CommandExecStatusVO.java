package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 命令执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 18:03
 */
@Data
public class CommandExecStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * exitCode
     */
    private Integer exitCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 使用时间
     */
    private Long used;

}
