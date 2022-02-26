package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemEnvRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id集合
     */
    private List<Long> idList;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 视图类型
     *
     * @see com.orion.ops.consts.env.EnvViewType
     */
    private Integer viewType;

}
