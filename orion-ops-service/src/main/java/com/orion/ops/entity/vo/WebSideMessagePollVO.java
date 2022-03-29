package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 站内信消息轮询返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/28 16:15
 */
@Data
public class WebSideMessagePollVO {

    /**
     * 未读数量
     */
    private Integer unreadCount;

    /**
     * 最大id
     */
    private Long maxId;

    /**
     * 最新消息
     */
    private List<WebSideMessageVO> newMessages;

}
