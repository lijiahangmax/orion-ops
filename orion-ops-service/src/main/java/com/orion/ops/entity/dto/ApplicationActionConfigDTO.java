package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 检查应用是否已配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 23:41
 */
@Data
@ApiModel(value = "检查应用是否已配置")
public class ApplicationActionConfigDTO {

    @ApiModelProperty(value = "appId")
    private Long appId;

    @ApiModelProperty(value = "构建阶段数量")
    private Integer buildStageCount;

    @ApiModelProperty(value = "发布阶段数量")
    private Integer releaseStageCount;

}
