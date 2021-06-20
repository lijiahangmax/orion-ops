package com.orion.ops.entity.request;

import com.orion.ops.consts.tail.FileTailType;
import lombok.Data;

/**
 * 文件tail 请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:52
 */
@Data
public class FileTailRequest {

    /**
     * relId
     */
    private Long relId;

    /**
     * @see FileTailType
     */
    private Integer type;

    /**
     * 文件尾部偏移行
     */
    private Integer offset;

    /**
     * 编码集
     */
    private String charset;

}
