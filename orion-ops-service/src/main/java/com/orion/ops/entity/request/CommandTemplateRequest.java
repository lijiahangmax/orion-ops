package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命令模板请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 18:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommandTemplateRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * type
     */
    private Integer type;

    /**
     * 模板值
     */
    private String value;

    /**
     * 命令描述
     */
    private String description;

}
