package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 站内信请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WebSideMessageRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id list
     */
    private List<Long> idList;

    /**
     * 消息分类
     *
     * @see com.orion.ops.consts.message.MessageClassify
     */
    private Integer classify;

    /**
     * 消息类型
     *
     * @see com.orion.ops.consts.message.MessageType
     */
    private Integer type;

    /**
     * 是否已读 1未读 2已读
     *
     * @see com.orion.ops.consts.message.ReadStatus
     */
    private Integer status;

    /**
     * 最大id
     */
    private Long maxId;

}
