package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 文件tail 返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
public class FileTailVO {

    /**
     * token
     */
    private String token;

    /**
     * 机器
     */
    private String host;

    /**
     * 文件
     */
    private String file;

    /**
     * offset
     */
    private Integer offset;

    /**
     * 编码集
     */
    private String charset;

}
