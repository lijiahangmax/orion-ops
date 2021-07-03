package com.orion.ops.entity.request;

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
     * @see com.orion.ops.consts.tail.FileTailType
     */
    private Integer type;

    /**
     * tail的文件路径
     *
     * @see com.orion.ops.consts.tail.FileTailType#LOCAL_FILE
     * @see com.orion.ops.consts.tail.FileTailType#REMOTE_FILE
     */
    private String path;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 文件尾部偏移行
     */
    private Integer offset;

    /**
     * 编码集
     */
    private String charset;

}
