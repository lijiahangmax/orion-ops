package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "命令模板请求")
public class CommandTemplateRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板值")
    private String value;

    @ApiModelProperty(value = "模板描述")
    private String description;

    @ApiModelProperty(value = "是否省略值")
    private boolean omitValue;

}
