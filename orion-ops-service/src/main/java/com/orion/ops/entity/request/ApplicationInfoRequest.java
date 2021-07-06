package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationInfoRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识符
     */
    private String tag;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序调整方向
     *
     * @see com.orion.ops.consts.Const#INCR
     * @see com.orion.ops.consts.Const#DECR
     */
    private Integer sortAdjust;

}
