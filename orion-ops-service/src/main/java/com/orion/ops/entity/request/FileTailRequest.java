package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件tail 请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTailRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 文件尾部偏移行
     *
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * 编码集
     *
     * @see com.orion.ops.consts.Const#UTF_8
     */
    private String charset;

    /**
     * tail 命令
     */
    private String command;

    /**
     * 宿主机文件追踪类型 tracker/tail
     *
     * @see com.orion.ops.consts.tail.FileTailMode
     */
    private String tailMode;

    /**
     * relId
     */
    private Long relId;

    /**
     * @see com.orion.ops.consts.tail.FileTailType
     */
    private Integer type;

    /**
     * token
     */
    private String token;

}
