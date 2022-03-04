package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
     * id
     */
    private List<Long> idList;

    /**
     * name
     */
    private String name;

    /**
     * 模板值
     */
    private String value;

    /**
     * 命令描述
     */
    private String description;

    /**
     * 是否省略值
     */
    private boolean omitValue;

}
