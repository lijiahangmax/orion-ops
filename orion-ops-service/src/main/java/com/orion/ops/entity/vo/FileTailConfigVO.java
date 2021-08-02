package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * tail 配置vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/2 0:50
 */
@Data
public class FileTailConfigVO {

    /**
     * offset
     *
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * charset
     *
     * @see com.orion.ops.consts.Const#UTF_8
     */
    private String charset;

}
