package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用环境变量请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用环境变量请求")
public class ApplicationEnvRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id集合")
    private List<Long> idList;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

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

    @ApiModelProperty(value = "目标环境id")
    private List<Long> targetProfileIdList;

}
