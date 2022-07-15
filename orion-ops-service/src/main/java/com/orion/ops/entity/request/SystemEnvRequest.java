package com.orion.ops.entity.request;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统环境变量请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "系统环境变量请求")
public class SystemEnvRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.consts.env.EnvViewType
     */
    @ApiModelProperty(value = "视图类型")
    private Integer viewType;

}
