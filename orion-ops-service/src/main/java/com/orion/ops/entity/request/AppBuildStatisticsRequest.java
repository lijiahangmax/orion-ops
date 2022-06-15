package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用构建统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 17:02
 */
@Data
@ApiModel(value = "应用构建统计")
public class AppBuildStatisticsRequest {

    @ApiModelProperty(value = "appId")
    private Long appId;

    @ApiModelProperty(value = "profileId")
    private Long profileId;

}
