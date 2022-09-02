package com.orion.ops.entity.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用环境缓存
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
@ApiModel(value = "应用环境缓存")
public class ApplicationProfileDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "环境名称")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
    private String profileTag;

}
