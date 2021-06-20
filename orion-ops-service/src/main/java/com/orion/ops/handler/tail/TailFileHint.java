package com.orion.ops.handler.tail;

import lombok.Data;

/**
 * 文件tail 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 16:34
 */
@Data
public class TailFileHint {

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 文件
     */
    private String path;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 尾行偏移量
     */
    private Integer offset;

    /**
     * 编码格式
     */
    private String charset;

}
