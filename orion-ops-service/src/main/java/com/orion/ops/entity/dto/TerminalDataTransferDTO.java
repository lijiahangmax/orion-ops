package com.orion.ops.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 终端 ws 数据传输
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/16 22:39
 */
@Data
public class TerminalDataTransferDTO implements Serializable {

    /**
     * session id
     */
    private String id;

    /**
     * 操作
     */
    private String operate;

    /**
     * body
     */
    private String body;

}
