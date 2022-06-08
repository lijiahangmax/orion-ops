package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 配置ip过滤器请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:13
 */
@Data
@ApiModel(value = "配置ip过滤器请求")
public class ConfigIpListRequest {

    @ApiModelProperty(value = "ip白名单")
    private String whiteIpList;

    @ApiModelProperty(value = "ip黑名单")
    private String blackIpList;

    @ApiModelProperty(value = "是否启用ip过滤器")
    private Boolean enableIpFilter;

    @ApiModelProperty(value = "是否启用ip白名单")
    private Boolean enableWhiteIpList;

}
