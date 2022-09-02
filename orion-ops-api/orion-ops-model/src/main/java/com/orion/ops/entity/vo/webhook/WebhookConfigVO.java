package com.orion.ops.entity.vo.webhook;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * webhook配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 18:01
 */
@Data
@ApiModel(value = "webhook配置响应")
@SuppressWarnings("ALL")
public class WebhookConfigVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "url")
    private String url;

    /**
     * @see com.orion.ops.constant.webhook.WebhookType
     */
    @ApiModelProperty(value = "类型 10: 钉钉机器人")
    private Integer type;

    @ApiModelProperty(value = "配置项 json")
    private String config;

}
