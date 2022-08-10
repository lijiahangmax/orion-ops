package com.orion.ops.entity.request.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用发布统计请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:46
 */
@Data
@ApiModel(value = "应用发布统计请求")
public class AppReleaseStatisticsRequest {

    @ApiModelProperty(value = "appId")
    private Long appId;

    @ApiModelProperty(value = "profileId")
    private Long profileId;

}
