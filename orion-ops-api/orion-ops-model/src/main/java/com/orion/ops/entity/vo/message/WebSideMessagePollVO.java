package com.orion.ops.entity.vo.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 站内信消息轮询响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/28 16:15
 */
@Data
@ApiModel(value = "站内信消息轮询响应")
public class WebSideMessagePollVO {

    @ApiModelProperty(value = "未读数量")
    private Integer unreadCount;

    @ApiModelProperty(value = "最大id")
    private Long maxId;

    @ApiModelProperty(value = "最新消息")
    private List<WebSideMessageVO> newMessages;

}
