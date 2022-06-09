package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ip过滤器配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:09
 */
@Data
@ApiModel(value = "ip过滤器配置响应")
public class IpListConfigVO {

    @ApiModelProperty(value = "当前ip")
    private String currentIp;

    @ApiModelProperty(value = "当前ip位置")
    private String ipLocation;

    @ApiModelProperty(value = "ip白名单")
    private String whiteIpList;

    @ApiModelProperty(value = "ip黑名单")
    private String blackIpList;

    @ApiModelProperty(value = "是否启用ip过滤器")
    private Boolean enableIpFilter;

    @ApiModelProperty(value = "是否启用ip白名单")
    private Boolean enableWhiteIpList;

}
