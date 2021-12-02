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
     * 版本控制id
     */
    private Long vcsId;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序调整方向
     *
     * @see com.orion.ops.consts.Const#INCREMENT
     * @see com.orion.ops.consts.Const#DECREMENT
     */
    private Integer sortAdjust;

    /**
     * 机器id
     */
    private Long machineId;

}
