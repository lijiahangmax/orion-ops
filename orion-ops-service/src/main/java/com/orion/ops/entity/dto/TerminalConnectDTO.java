package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 机器连接
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 20:12
 */
@Data
public class TerminalConnectDTO {

    /**
     * 登陆token
     */
    private String loginToken;

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

}
