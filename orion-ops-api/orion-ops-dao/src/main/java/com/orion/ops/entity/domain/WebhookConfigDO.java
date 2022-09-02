package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * webhook 配置
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("webhook_config")
@ApiModel(value = "WebhookConfigDO对象", description = "webhook 配置")
@SuppressWarnings("ALL")
public class WebhookConfigDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "名称")
    @TableField("webhook_name")
    private String webhookName;

    @ApiModelProperty(value = "url")
    @TableField("webhook_url")
    private String webhookUrl;

    /**
     * @see com.orion.ops.constant.webhook.WebhookType
     */
    @ApiModelProperty(value = "类型 10: 钉钉机器人")
    @TableField("webhook_type")
    private Integer webhookType;

    @ApiModelProperty(value = "配置项 json")
    @TableField("webhook_config")
    private String webhookConfig;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

}
