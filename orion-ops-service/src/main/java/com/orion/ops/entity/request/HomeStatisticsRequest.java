package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 主页统计请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/25 16:34
 */
@Data
@ApiModel(value = "主页统计请求")
public class HomeStatisticsRequest {

    @ApiModelProperty(value = "profileId")
    private Long profileId;

}
